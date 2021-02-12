/*
 * Coin Collection, an Android app that helps users track the coins that they've collected
 * Copyright (C) 2010-2016 Andrew Williams
 *
 * This file is part of Coin Collection.
 *
 * Coin Collection is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Coin Collection is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Coin Collection.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.coincollection.CoinPageCreator;
import com.coincollection.CoinSlot;
import com.spencerpages.collections.AmericanEagleSilverDollars;
import com.spencerpages.collections.AmericanInnovationDollars;
import com.spencerpages.collections.BarberDimes;
import com.spencerpages.collections.BarberHalfDollars;
import com.spencerpages.collections.BarberQuarters;
import com.spencerpages.collections.BuffaloNickels;
import com.spencerpages.collections.EisenhowerDollar;
import com.spencerpages.collections.FirstSpouseGoldCoins;
import com.spencerpages.collections.FranklinHalfDollars;
import com.spencerpages.collections.IndianHeadCents;
import com.spencerpages.collections.JeffersonNickels;
import com.spencerpages.collections.KennedyHalfDollars;
import com.spencerpages.collections.LibertyHeadNickels;
import com.spencerpages.collections.LincolnCents;
import com.spencerpages.collections.MercuryDimes;
import com.spencerpages.collections.MorganDollars;
import com.spencerpages.collections.NationalParkQuarters;
import com.spencerpages.collections.NativeAmericanDollars;
import com.spencerpages.collections.PeaceDollars;
import com.spencerpages.collections.PresidentialDollars;
import com.spencerpages.collections.RooseveltDimes;
import com.spencerpages.collections.StandingLibertyQuarters;
import com.spencerpages.collections.StateQuarters;
import com.spencerpages.collections.SusanBAnthonyDollars;
import com.spencerpages.collections.WalkingLibertyHalfDollars;
import com.spencerpages.collections.WashingtonQuarters;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;

public class CollectionCreationTests extends BaseTestCase {

    /**
     * For AmericanEagleSilverDollars
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_AmericanEagleSilverDollarsCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        AmericanEagleSilverDollars coinClass = new AmericanEagleSilverDollars();
        coinClass.getCreationParameters(parameters);

        // Show Burnished, Expected Result
        Object[][] tests = {
                {false, 35},
                {true,  35 + 4},
        };

        for(Object[] test : tests) {
            parameters.put(CoinPageCreator.OPT_CHECKBOX_2, test[0]);
            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[1], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For AmericanInnovationDollars
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_AmericanInnovationDollarsCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        AmericanInnovationDollars coinClass = new AmericanInnovationDollars();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, Expected Result
        Object[][] tests = {
                {false, true,  true,  5},
                {false, false, true,  5},
                {true,  false, false, 0},
                {true,  true,  false, 5},
                {true,  false, true,  5},
                {true,  true,  true,  5 + 5},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[3], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For BarberDimes
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_BarberDimesCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        BarberDimes coinClass = new BarberDimes();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, S, O, Expected Result
        Object[][] tests = {
                {false, true,  false,  true,  false, 25},
                {false, false, false,  false, false, 25},
                {true,  true,  false,  false, false, 25},
                {true,  false, true,   false, false, 8},
                {true,  false, false,  true,  false, 24},
                {true,  false, false,  false, true,  17},
                {true,  true,  true,   true,  true,  25 + 8 + 24 + 17},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, test[3]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_4, test[4]);

            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[5], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For BarberHalfDollars
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_BarberHalfDollarsCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        BarberHalfDollars coinClass = new BarberHalfDollars();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, S, O, Expected Result
        Object[][] tests = {
                {false, true,  false,  true,  false, 24},
                {false, false, false,  false, false, 24},
                {true,  true,  false,  false, false, 24},
                {true,  false, true,   false, false, 7},
                {true,  false, false,  true,  false, 24},
                {true,  false, false,  false, true,  18},
                {true,  true,  true,   true,  true,  24 + 7 + 24 + 18},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, test[3]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_4, test[4]);

            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[5], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For BarberQuarters
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_BarberQuartersCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        BarberQuarters coinClass = new BarberQuarters();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, S, O, Expected Result
        Object[][] tests = {
                {false, true,  false,  true,  false, 25},
                {false, false, false,  false, false, 25},
                {true,  true,  false,  false, false, 25},
                {true,  false, true,   false, false, 10},
                {true,  false, false,  true,  false, 21},
                {true,  false, false,  false, true,  18},
                {true,  true,  true,   true,  true,  25 + 10 + 21 + 18},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, test[3]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_4, test[4]);

            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[5], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For BuffaloNickels
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_BuffaloNickelsCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        BuffaloNickels coinClass = new BuffaloNickels();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, S, Expected Result
        Object[][] tests = {
                {false, true,  false,  true,  23},
                {false, false, false,  false, 23},
                {true,  true,  false,  false, 22},
                {true,  false, true,   false, 20},
                {true,  false, false,  true,  22},
                {true,  true,  true,   true,  22 + 20 + 22},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, test[3]);

            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[4], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For EisenhowerDollar
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_EisenhowerDollarCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        EisenhowerDollar coinClass = new EisenhowerDollar();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, Expected Result
        Object[][] tests = {
                {false, true,  true,  7},
                {false, false, true,  7},
                {true,  false, false, 0},
                {true,  true,  false, 7},
                {true,  false, true,  7},
                {true,  true,  true,  7 + 7},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[3], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }

        // Test special case of start year 1975/1976
        int[] years = {1975, 1976};
        for(int year : years)
        parameters.put(CoinPageCreator.OPT_START_YEAR, year);
        ArrayList<CoinSlot> coinList = new ArrayList<>();
        coinClass.populateCollectionLists(parameters, coinList);
        assertEquals(6, coinList.size());

        checkCreationParamsFromCoinList(coinList, coinClass);
    }

    /**
     * For FirstSpouseGoldCoins
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_FirstSpouseGoldCoinsCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        FirstSpouseGoldCoins coinClass = new FirstSpouseGoldCoins();
        coinClass.getCreationParameters(parameters);

        ArrayList<CoinSlot> coinList = new ArrayList<>();
        coinClass.populateCollectionLists(parameters, coinList);
        assertEquals(41, coinList.size());

        checkCreationParamsFromCoinList(coinList, coinClass);
    }

    /**
     * For FranklinHalfDollars
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_FranklinHalfDollarsCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        FranklinHalfDollars coinClass = new FranklinHalfDollars();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, S, Expected Result
        Object[][] tests = {
                {false, true,  false,  true,  16},
                {false, false, false,  false, 16},
                {true,  true,  false,  false, 16},
                {true,  false, true,   false, 14},
                {true,  false, false,  true,  5},
                {true,  true,  true,   true,  16 + 14 + 5},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, test[3]);

            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[4], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For IndianHeadCents
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_IndianHeadCentsCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        IndianHeadCents coinClass = new IndianHeadCents();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, S, Expected Result
        Object[][] tests = {
                {false, true,  true,  51},
                {false, false, true,  51},
                {true,  false, false, 0},
                {true,  true,  false, 53},
                {true,  false, true,  2},
                {true,  true,  true,  53 + 2},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, test[2]);
            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[3], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For JeffersonNickels
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_JeffersonNickelsCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        JeffersonNickels coinClass = new JeffersonNickels();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, S, Expected Result
        Object[][] tests = {
                {false, true,  false,  true,  85},
                {false, false, false,  false, 85},
                {true,  true,  false,  false, 82},
                {true,  false, true,   false, 82},
                {true,  false, false,  true,  19},
                {true,  true,  true,   true,  82 + 82 + 19},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, test[3]);

            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[4], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For KennedyHalfDollars
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_KennedyHalfDollarsCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        KennedyHalfDollars coinClass = new KennedyHalfDollars();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, Expected Result
        Object[][] tests = {
                {false, true,  true,  56},
                {false, false, true,  56},
                {true,  false, false, 0},
                {true,  true,  false, 53},
                {true,  false, true,  53},
                {true,  true,  true,  53 + 53},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[3], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }

        // Test special case of start year 1975/1976
        int[] years = {1975, 1976};
        for(int year : years)
            parameters.put(CoinPageCreator.OPT_START_YEAR, year);
        ArrayList<CoinSlot> coinList = new ArrayList<>();
        coinClass.populateCollectionLists(parameters, coinList);
        assertEquals(90, coinList.size());

        checkCreationParamsFromCoinList(coinList, coinClass);
    }

    /**
     * For LibertyHeadNickels
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_LibertyHeadNickelsCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        LibertyHeadNickels coinClass = new LibertyHeadNickels();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, S, Expected Result
        Object[][] tests = {
                {false, true,  false,  true,  31},
                {false, false, false,  false, 31},
                {true,  true,  false,  false, 31},
                {true,  false, true,   false, 1},
                {true,  false, false,  true,  1},
                {true,  true,  true,   true,  31 + 1 + 1},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, test[3]);

            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[4], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For LincolnCents
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_LincolnCentsCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        LincolnCents coinClass = new LincolnCents();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, S, Expected Result
        Object[][] tests = {
                {false, true,  false,  true,  116},
                {false, false, false,  false, 116},
                {true,  true,  false,  false, 116},
                {true,  false, true,   false, 108},
                {true,  false, false,  true,  51},
                {true,  true,  true,   true,  116 + 108 + 51},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, test[3]);

            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[4], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For MercuryDimes
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_MercuryDimesCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        MercuryDimes coinClass = new MercuryDimes();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, S, Expected Result
        Object[][] tests = {
                {false, true,  false,  true,  27},
                {false, false, false,  false, 27},
                {true,  true,  false,  false, 27},
                {true,  false, true,   false, 25},
                {true,  false, false,  true,  25},
                {true,  true,  true,   true,  27 + 25 + 25},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, test[3]);

            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[4], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For MorganDollars
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_MorganDollarsCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        MorganDollars coinClass = new MorganDollars();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, S, O, CC, Expected Result
        Object[][] tests = {
                {false, true,  false,  true,  false, true,  28},
                {false, false, true,   false, true,  false, 28},
                {true,  true,  false,  false, false, false, 28},
                {true,  false, true,   false, false, false, 1},
                {true,  false, false,  true,  false, false, 28},
                {true,  false, false,  false, true,  false, 26},
                {true,  false, false,  false, false, true,  13},
                {true,  true,  true,   true,  true,  true,  28 + 1 + 28 + 26 + 13},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, test[3]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_4, test[4]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_5, test[5]);

            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[6], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For NationalParkQuarters
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_NationalParkQuartersCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        NationalParkQuarters coinClass = new NationalParkQuarters();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, Expected Result
        Object[][] tests = {
                {false, true,  true,  55},
                {false, false, true,  55},
                {true,  false, false, 0},
                {true,  true,  false, 55},
                {true,  false, true,  55},
                {true,  true,  true,  55 + 55},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[3], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For NativeAmericanDollars
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_NativeAmericanDollarsCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        NativeAmericanDollars coinClass = new NativeAmericanDollars();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, Expected Result
        Object[][] tests = {
                {false, true,  true,  21},
                {false, false, true,  21},
                {true,  false, false, 0},
                {true,  true,  false, 21},
                {true,  false, true,  21},
                {true,  true,  true,  21 + 21},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[3], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For PeaceDollars
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_PeaceDollarsCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        PeaceDollars coinClass = new PeaceDollars();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, S, Expected Result
        Object[][] tests = {
                {false, true,  false,  true,  10},
                {false, false, false,  false, 10},
                {true,  true,  false,  false, 10},
                {true,  false, true,   false, 5},
                {true,  false, false,  true,  9},
                {true,  true,  true,   true,  10 + 5 + 9},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, test[3]);

            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[4], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For PresidentialDollars
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_PresidentialDollarsCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        PresidentialDollars coinClass = new PresidentialDollars();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, Expected Result
        Object[][] tests = {
                {false, true,  true,  39},
                {false, false, true,  39},
                {true,  false, false, 0},
                {true,  true,  false, 39},
                {true,  false, true,  39},
                {true,  true,  true,  39 + 39},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[3], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For RooseveltDimes
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_RooseveltDimesCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        RooseveltDimes coinClass = new RooseveltDimes();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, S, Expected Result
        Object[][] tests = {
                {false, true,  false,  true,  75},
                {false, false, false,  false, 75},
                {true,  true,  false,  false, 75},
                {true,  false, true,   false, 72},
                {true,  false, false,  true,  10},
                {true,  true,  true,   true,  75 + 72 + 10},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, test[3]);

            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[4], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For StandingLibertyQuarters
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_StandingLibertyQuartersCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        StandingLibertyQuarters coinClass = new StandingLibertyQuarters();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, S, Expected Result
        Object[][] tests = {
                {false, true,  false,  true,  15},
                {false, false, false,  false, 15},
                {true,  true,  false,  false, 15},
                {true,  false, true,   false, 10},
                {true,  false, false,  true,  12},
                {true,  true,  true,   true,  15 + 10 + 12},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, test[3]);

            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[4], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For StateQuarters
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_StateQuartersCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        StateQuarters coinClass = new StateQuarters();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, Show Territories, Expected Result
        Object[][] tests = {
                {false, true,  true,   false, 50},
                {false, false, false,  true,  56},
                {true,  true,  false,  false, 50},
                {true,  false, true,   false, 50},
                {true,  true,  false,  true,  56},
                {true,  false, true,   true,  56},
                {true,  true,  true,   true,  56 + 56},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            parameters.put(CoinPageCreator.OPT_CHECKBOX_1, test[3]);

            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[4], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For SusanBAnthonyDollars
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_SusanBAnthonyDollarsCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        SusanBAnthonyDollars coinClass = new SusanBAnthonyDollars();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, S, Expected Result
        Object[][] tests = {
                {false, true,  false,  true,  4},
                {false, false, false,  false, 4},
                {true,  true,  false,  false, 4},
                {true,  false, true,   false, 4},
                {true,  false, false,  true,  3},
                {true,  true,  true,   true,  4 + 4 + 3},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, test[3]);

            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[4], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For WalkingLibertyHalfDollars
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_WalkingLibertyHalfDollarsCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        WalkingLibertyHalfDollars coinClass = new WalkingLibertyHalfDollars();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, S, Expected Result
        Object[][] tests = {
                {false, true,  false,  true,  25},
                {false, false, false,  false, 25},
                {true,  true,  false,  false, 20},
                {true,  false, true,   false, 21},
                {true,  false, false,  true,  24},
                {true,  true,  true,   true,  20 + 21 + 24},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, test[3]);

            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[4], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }
    }

    /**
     * For WashingtonQuarters
     * - Test that the number of coins is correct upon collection creation
     */
    @Test
    public void test_WashingtonQuartersCreationCounts() {

        HashMap<String, Object> parameters = new HashMap<>();
        WashingtonQuarters coinClass = new WashingtonQuarters();
        coinClass.getCreationParameters(parameters);

        // Show Mint Marks, P, D, S, Expected Result
        Object[][] tests = {
                {false, true,  false,  true,  65},
                {false, false, false,  false, 65},
                {true,  true,  false,  false, 65},
                {true,  false, true,   false, 61},
                {true,  false, false,  true,  20},
                {true,  true,  true,   true,  65 + 61 + 20},
        };

        for(Object[] test : tests){
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, test[0]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, test[1]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, test[2]);
            parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, test[3]);

            ArrayList<CoinSlot> coinList = new ArrayList<>();
            coinClass.populateCollectionLists(parameters, coinList);
            assertEquals(test[4], coinList.size());

            checkCreationParamsFromCoinList(coinList, coinClass);
        }

        // Test special case of start year 1975/1976
        int[] years = {1975, 1976};
        for(int year : years)
            parameters.put(CoinPageCreator.OPT_START_YEAR, year);
        ArrayList<CoinSlot> coinList = new ArrayList<>();
        coinClass.populateCollectionLists(parameters, coinList);
        assertEquals(46, coinList.size());

        checkCreationParamsFromCoinList(coinList, coinClass);
    }
}