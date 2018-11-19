

find ./ -name ".idea"|xargs rm -rf
find ./ -name ".iml"|xargs rm -rf
find ./ -name "target"|xargs rm -rf
find ./ -name ".settings"|xargs rm -rf
find ./ -name "logs/*"|xargs rm -rf
find ./ -name "*.versionsBackup"|xargs rm -rf


