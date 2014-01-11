package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import data.impl.FileStorage;
import data.impl.RawData;
import data.interfaces.IData;
import data.interfaces.IDataStorage;

public class RawDataFileTest {

	private IDataStorage store;
	private IData data; 
	static String fn = "e:\\abc.txt";
	@Before
	public void setUp() throws Exception {
		data = new RawData();
		store = new FileStorage();
		store.setStringConnection(fn);
	}

//	@AfterClass
//	public static void tearDown() throws Exception {
//		Files.deleteIfExists(Paths.get(fn));
//	}

	@Test
	public void test() {
		
		
		try {
			store.Connect();
			assertTrue(Files.exists(Paths.get(fn)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testWriteData(){
		if(data!=null && store!=null){
			String t = "Tekst próbny";
			data.Load(t);
			System.out.println(data.getValues(null));
			assertNotNull(data.getValues(null));
			assertTrue(data.getValues(null).equals(t));
			try {
				store.Connect();
				store.writeData(data);
				assertEquals(t, Files.readAllLines(Paths.get(fn), Charset.defaultCharset()).get(0));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
