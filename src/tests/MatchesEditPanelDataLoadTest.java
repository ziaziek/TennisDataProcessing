/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import data.MatchesExt;
import errors.DataDealerReadException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author Przemo
 */
public class MatchesEditPanelDataLoadTest {
    
    public MatchesEditPanelDataLoadTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    @Test
    public void loadTest(){
        try {
            Object[][] data = MatchesExt.getGamesForMatch(1);
            assertNotNull(data);
            assertTrue(data.length>0);
            assertEquals(1, data[0][0]);
            assertNull(data[0][2]); //player's id is different than the opponent's id
            assertTrue(data[0][1]!=data[0][2]); //player's id is different than the opponent's id
        } catch (DataDealerReadException ex) {
            Logger.getLogger(MatchesEditPanelDataLoadTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}