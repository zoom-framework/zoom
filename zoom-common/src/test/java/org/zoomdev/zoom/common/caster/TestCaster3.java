package org.zoomdev.zoom.common.caster;

import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestCaster3  {

    @Test(expected = Caster.CasterException.class)
    public void testFirstVisit(){

        ValueCaster valueCaster = Caster.wrapFirstVisit(
                Integer.class
        );

        Caster.WrapExceptionCaster wrapExceptionCaster = (Caster.WrapExceptionCaster)valueCaster;
        Caster.FirstVisitValueCaster firstVisitValueCaster = (Caster.FirstVisitValueCaster) wrapExceptionCaster.valueCaster;

        assertNull(firstVisitValueCaster.caster);

        assertEquals(valueCaster.to(null),null);

        assertNull(firstVisitValueCaster.caster);

        assertEquals(valueCaster.to("1"),1);

        assertNotNull(firstVisitValueCaster.caster);

        ///error

        valueCaster.to(1.0f);

        ValueCaster valueCaster2 = Caster.wrap(null,Integer.class);
        assertTrue(valueCaster instanceof Caster.WrapExceptionCaster);
        Caster.WrapExceptionCaster wrapExceptionCaster1 = (Caster.WrapExceptionCaster)valueCaster2;
        Caster.FirstVisitValueCaster firstVisitValueCaster2 = (Caster.FirstVisitValueCaster) wrapExceptionCaster1.valueCaster;

        assertTrue(firstVisitValueCaster2 == firstVisitValueCaster);
    }

    @Test(expected = Caster.CasterException.class)
    public void testWrapToTypeIsNull(){
        Caster.wrap(null);
    }


    @Test
    public void testWrapAny(){

        ValueCaster valueCaster = Caster.wrap(Date.class);



        assertEquals(
                valueCaster.to("20001010"),
                Caster.to("20001010",Date.class)
        );



    }


    @Test(expected = Caster.CasterException.class)
    public void wrapNull(){

        Caster.wrap(Integer.class,null);

    }


    @Test
    public void wrapSame(){

        ValueCaster valueCaster = Caster.wrap( Integer.class,Integer.class );

        assertTrue(valueCaster instanceof Caster.EmptyValueCaster);

        valueCaster = Caster.wrap( Number.class,Integer.class );

        assertTrue(valueCaster instanceof Caster.EmptyValueCaster);

        valueCaster = Caster.wrap( Integer.class,Double.class );

        assertTrue(!(valueCaster instanceof Caster.EmptyValueCaster));


    }


    @Test
    public void testWrapPrimitive(){

        ValueCaster valueCaster = Caster.wrap( int.class,Double.class );

        assertEquals(
                valueCaster.to(null),
                null
        );


        assertEquals(valueCaster.to(1),1.0D);
    }



    @Test
    public void testWrapNull(){

        ValueCaster valueCaster = Caster.wrap(
                Date.class,String.class
        );

        assertTrue(valueCaster instanceof Caster.CheckNullCaster);


    }

}
