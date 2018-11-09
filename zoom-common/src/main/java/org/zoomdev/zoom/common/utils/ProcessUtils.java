package org.zoomdev.zoom.common.utils;

import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.io.Io;
import org.zoomdev.zoom.common.queue.ServiceThread;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.io.Io;
import org.zoomdev.zoom.common.queue.ServiceThread;

import java.io.*;

/**
 * 使用一个线程对Process进行监控
 */
public class ProcessUtils {

    private static final Log log = LogFactory.getLog(ProcessUtils.class);

    public static ProcessController exec(String command) throws IOException {
        return exec(command,new LogProcessListener());
    }

    public static ProcessController exec(String command,ProcessListener processListener) throws IOException {
        Process process =  Runtime.getRuntime().exec(command);
        return new ProcessControllerImpl(process,processListener);
    }

    public static interface ProcessController extends Destroyable {
        void write(String command) throws IOException;
        int waitFor() throws InterruptedException;
    }


    public static interface ProcessListener{
        void onOutput(String line);
        void onExit(int code);
        void onException(Exception e);
    }

    static class LogProcessListener implements ProcessListener{

        @Override
        public void onOutput(String line) {
            System.out.println(line);
        }

        @Override
        public void onExit(int code) {
            log.info("Process exit with code:"+code);
        }

        @Override
        public void onException(Exception e) {
            log.error("Process exception",e);
        }

    }

    static class ProcessControllerImpl implements ProcessController {
        private final OutputStream outputStream;
        private final InputStream inputStream;
        private final InputStream errorStream;
        private ServiceThread readThread;
        private ServiceThread errorThread;
        final BufferedReader reader;
        final BufferedReader errorReader;
        private ProcessListener processListener;
        private Writer writer;

        private Thread waitThread;


        public ProcessControllerImpl(Process process,ProcessListener processListener) {
            this.process = process;
            this.outputStream = process.getOutputStream();
            this.inputStream = process.getInputStream();
            this.errorStream = process.getErrorStream();
            this.processListener = processListener;

            reader = new BufferedReader(new InputStreamReader(inputStream));


            this.errorReader = new BufferedReader(new InputStreamReader(errorStream));
            this.readThread = new ServiceThread() {
                @Override
                protected boolean repetitionRun() {
                    return read(reader);
                }
            };
            this.readThread.start();
            this.errorThread = new ServiceThread() {
                @Override
                protected boolean repetitionRun() {
                    return read(errorReader);
                }
            };
            this.errorThread.start();
            this.waitThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        waitFor();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            this.waitThread.setDaemon(true);
            this.waitThread.start();

        }

        private void execute() {
            try {
                int ret = this.process.waitFor();
                processListener.onExit(ret);
            } catch (InterruptedException e) {

            }
        }

        private final Process process;


        @Override
        public void destroy() {
            if (this.process != null)
                this.process.destroy();
            Io.close(this.errorStream);
            Io.close(this.outputStream);
            Io.close(this.inputStream);
            Io.close(this.writer);
            Io.close(this.reader);
            Io.close(this.errorReader);
            if (this.readThread != null) this.readThread.stop();
            if (this.errorThread != null) this.errorThread.stop();
        }


        private boolean read(BufferedReader reader) {
            String line = null;
            try {
                line = reader.readLine();
                if(line == null){
                    execute();
                    return false;
                }
                processListener.onOutput(line);
                return true;
            } catch (IOException e) {

                processListener.onException(e);

                return false;
            }


        }


        @Override
        public synchronized void write(String command) throws IOException {
            outputStream.write((command + "\n").getBytes());
            outputStream.flush();

        }

        @Override
        public int waitFor() throws InterruptedException {
            int ret = this.process.waitFor();
            log.warn("Process has exit with code:" + ret);
            processListener.onExit(ret);
            return ret;
        }
    }


}
