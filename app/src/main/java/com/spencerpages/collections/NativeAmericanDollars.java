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

package com.spencerpages.collections;

import com.spencerpages.CoinPageCreator;
import com.spencerpages.R;

import java.util.ArrayList;
import java.util.HashMap;

public class NativeAmericanDollars extends CollectionInfo {

    private static final String COLLECTION_TYPE = "Sacagawea/Native American Dollars"; // Was: Sacagawea Dollars

    // Handle the ones needing the various native american coin images differently
    private static final String[] NATIVE_COIN_IDENTIFIERS = {
            "2009",
            "2010",
            "2011",
            "2012",
            "2013",
            "2014",
            "2015",
            "2016",
            "2017",
    };

    private static final Integer[][] NATIVE_IMAGE_IDENTIFIERS = {
            { R.drawable.native_2009_unc,       R.drawable.native_2009_unc_25},
            { R.drawable.native_2010_unc,       R.drawable.native_2010_unc_25},
            { R.drawable.native_2011_unc,       R.drawable.native_2011_unc_25},
            { R.drawable.native_2012_unc,       R.drawable.native_2012_unc_25},
            { R.drawable.native_2013_proof,     R.drawable.native_2013_proof_25},
            { R.drawable.native_2014_unc,       R.drawable.native_2014_unc_25},
            { R.drawable.native_2015_unc,       R.drawable.native_2015_unc_25},
            { R.drawable.native_2016_unc,       R.drawable.native_2016_unc_25},
            { R.drawable.native_2017_line_art,  R.drawable.native_2017_line_art_25},
    };

    private static final HashMap<String, Integer[]> NATIVE_INFO = new HashMap<>();

    static {
        // Populate the NATIVE_INFO HashMap for quick image ID lookups later
        for (int i = 0; i < NATIVE_COIN_IDENTIFIERS.length; i++){
            NATIVE_INFO.put(NATIVE_COIN_IDENTIFIERS[i], NATIVE_IMAGE_IDENTIFIERS[i]);
        }
    }

    private static final Integer START_YEAR = 2000;
    private static final Integer STOP_YEAR = CoinPageCreator.STILL_IN_PRODUCTION;

    private static final int OBVERSE_IMAGE_COLLECTED = R.drawable.obv_sacagawea_unc;
    private static final int OBVERSE_IMAGE_MISSING = R.drawable.obv_sacagawea_unc_25;

    private static final int REVERSE_IMAGE = R.drawable.rev_sacagawea_unc;

    public String getCoinType() { return COLLECTION_TYPE; }

    public int getCoinImageIdentifier() { return REVERSE_IMAGE; }

    public int getCoinSlotImage(String identifier, String mint, Boolean inCollection){

        if(NATIVE_INFO.containsKey(identifier)){
            return NATIVE_INFO.get(identifier)[inCollection ? 0 : 1 ];
        } else {
            return inCollection ? OBVERSE_IMAGE_COLLECTED : OBVERSE_IMAGE_MISSING;
        }
    }

    public void getCreationParameters(HashMap<String, Object> parameters) {

        parameters.put(CoinPageCreator.EDIT_DATE_RANGE, Boolean.FALSE);
        parameters.put(CoinPageCreator.START_YEAR, START_YEAR);
        parameters.put(CoinPageCreator.STOP_YEAR, STOP_YEAR);
        parameters.put(CoinPageCreator.SHOW_MINT_MARKS, Boolean.FALSE);
        parameters.put(CoinPageCreator.SHOW_P, Boolean.TRUE);
        parameters.put(CoinPageCreator.SHOW_D, Boolean.FALSE);
    }

    // TODO Perform validation and throw exception
    public void populateCollectionArrays(HashMap<String, Object> parameters,
                                         ArrayList<String> identifierList,
                                         ArrayList<String> mintList) {

        Integer startYear       = (Integer) parameters.get(CoinPageCreator.START_YEAR);
        Integer stopYear        = (Integer) parameters.get(CoinPageCreator.STOP_YEAR);
        Boolean showMintMarks   = (Boolean) parameters.get(CoinPageCreator.SHOW_MINT_MARKS);
        Boolean showP           = (Boolean) parameters.get(CoinPageCreator.SHOW_P);
        Boolean showD           = (Boolean) parameters.get(CoinPageCreator.SHOW_D);

        for(int i = startYear; i <= stopYear; i++){

            if(showMintMarks){
                if(showP){
                    identifierList.add(Integer.toString(i));
                    mintList.add(" P");
                }
            } else {
                identifierList.add(Integer.toString(i));
                mintList.add("");
            }

            if(showMintMarks && showD){
                identifierList.add(Integer.toString(i));
                mintList.add(" D");
            }
        }
    }
}