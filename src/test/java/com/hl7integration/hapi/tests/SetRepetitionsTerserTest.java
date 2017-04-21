package com.hl7integration.hapi.tests;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.GenericModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.parser.ParserConfiguration;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import org.junit.Test;
//import org.relaxng.datatype.ValidationContext;

import java.io.IOException;
import java.util.concurrent.ExecutorService;


public class SetRepetitionsTerserTest {

@Test
public void testSetManualRepetitions() {
    try {
        String m = "MSH|^~\\&|hl7Integration|hl7Integration|||||ADT^A01|||2.3|\r" +
                "EVN|A01|20130617154644\r" +
                "PID|1|465 306 5961||407623|Wood^Patrick^^^MR||19700101|1||||||||||\r" +
                "PV1|1||Location||||||||||||||||261938_6_201306171546|||||||||||||||||||||||||20130617134644|||||||||";

        HapiContext hc = new DefaultHapiContext();
        ExecutorService es = hc.getExecutorService(); // to avoid npe when closing context should be fixed https://sourceforge.net/p/hl7api/bugs/223/
        ParserConfiguration pc = hc.getParserConfiguration();
        PipeParser pipeParser = hc.getPipeParser();
        Message message = pipeParser.parse(m);
        Terser terser = new Terser(message);
        //Add first Address
        terser.set("/.PID-11(0)-1", "13 Oxford Road");
        terser.set("/.PID-11(0)-3", "Oxford");
        //Add second Address
        terser.set("/.PID-11(1)-1", "16 London Road");
        terser.set("/.PID-11(1)-3", "London");

        pc.addForcedEncode("PID-26-1");
        System.out.println(message.encode().replaceAll("\r", "\r\n"));
        hc.close();
    } catch (HL7Exception e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    @Test
public void testSetLoopRepetitions() {
    try {
        String m = "MSH|^~\\&|hl7Integration|hl7Integration|||||ADT^A01|||2.3|\r" +
                "EVN|A01|20130617154644\r" +
                "PID|1|465 306 5961||407623|Wood^Patrick^^^MR||19700101|1||||||||||\r" +
                "PV1|1||Location||||||||||||||||261938_6_201306171546|||||||||||||||||||||||||20130617134644|||||||||";

        //Create the Terser
        PipeParser pipeParser = new PipeParser();
        Message message = pipeParser.parse(m);
        Terser terser = new Terser(message);

        //Add 5 addresses
        int maxRepetitions = 5;
        String street;
        String city;
        for (int i= 0; i < maxRepetitions; i++ ){
            street = "Street" + Integer.toString(i);
            terser.set("/.PID-11("+i+")-1", street);
            city = "City" + Integer.toString(i);
            terser.set("/.PID-11("+i+")-3", city);
        }

        System.out.println(message.encode());

    } catch (HL7Exception e) {
        e.printStackTrace();
    }

}

    @Test
    public void testSetSegmentRepetitions(){
        try {

            String m = "MSH|^~\\&|hl7Integration|hl7Integration|||||ADT^A01|||2.3|\r" +
                    "EVN|A01|20130617154644\r" +
                    "PID|1|465 306 5961||407623|Wood^Patrick^^^MR||19700101|1||||||||||\r" +
                    "PV1|1||Location||||||||||||||||261938_6_201306171546|||||||||||||||||||||||||20130617134644|||||||||";

            //Create the Terser
            PipeParser pipeParser = new PipeParser();
            Message message = pipeParser.parse(m);
            Terser terser = new Terser(message);

            //Add first next of Kin
            //NK1|1|Jones^Joe|Father||||||
            terser.set("/.NK1(1)-1-1", "1");
            terser.set("/.NK1(1)-2-1", "Jones");
            terser.set("/.NK1(1)-2-2", "Joe");
            terser.set("/.NK1(1)-3-1", "Father");

            //Add Second next of kin
            //NK1|2|Hall^Anna|Mother
            terser.set("/.NK1(2)-1-1", "2");
            terser.set("/.NK1(2)-2-1", "Hall");
            terser.set("/.NK1(2)-2-2", "Anna");
            terser.set("/.NK1(2)-3-1", "Mother");


            System.out.println(message.encode());

        } catch (HL7Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSetSegmentRepetitionsNoVersionId(){
        try {

            String m = "MSH|^~\\&|hl7Integration|hl7Integration|||||ADT^A01|msh10|msh11|msh12|20160101102245\r" +
                    "EVN|A01|20130617154644\r" +
                    "PID|1|465 306 5961||407623|Wood^Patrick^^^MR||19700101|1||||||||||\r" +
                    "PV1|1||Location||||||||||||||||261938_6_201306171546|||||||||||||||||||||||||20130617134644|||||||||";

            //Create the Terser
            final ModelClassFactory modelClassFactory = new GenericModelClassFactory();
            final PipeParser pipeParser = new PipeParser(modelClassFactory);
            pipeParser.getParserConfiguration()
                    .setAllowUnknownVersions(true);

            final Message message = pipeParser.parse(m);
            Terser terser = new Terser(message);

            //Add first next of Kin
            //NK1|1|Jones^Joe|Father||||||
            System.out.println("msh-4: "+terser.get("MSH-4"));


            System.out.println(message.encode());

        } catch (HL7Exception e) {
            e.printStackTrace();
        }

    }
}
