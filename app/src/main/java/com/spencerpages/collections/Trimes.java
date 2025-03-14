package com.spencerpages.collections;

import android.database.sqlite.SQLiteDatabase;

import com.coincollection.CoinPageCreator;
import com.coincollection.CoinSlot;
import com.coincollection.CollectionInfo;
import com.coincollection.CollectionListInfo;
import com.spencerpages.R;

import java.util.ArrayList;
import java.util.HashMap;


public class Trimes extends CollectionInfo {

    public static final String COLLECTION_TYPE = "Three Cents";

    private static final Object[][] COIN_IDENTIFIERS = {
            {"Silver", R.drawable.annc_us_1854_3c_three_cent__silver__tyii_},
            {"Nickel", R.drawable.annc_us_1865_3c_three_cent__nickel},
    };

    private static final Object[][] COIN_IMG_IDS = {
            {"Silver", R.drawable.annc_us_1854_3c_three_cent__silver__tyii_},
            {"Nickel", R.drawable.annc_us_1865_3c_three_cent__nickel},
    };


    private static final HashMap<String, Integer> COIN_MAP = new HashMap<>();

    static {
        for (Object[] coinData : COIN_IDENTIFIERS) {COIN_MAP.put((String) coinData[0], (Integer) coinData[1]);}
    }

    private static final int REVERSE_IMAGE = R.drawable.annc_us_1865_3c_three_cent__nickel;

    @Override
    public String getCoinType() {
        return COLLECTION_TYPE;
    }

    @Override
    public int getCoinImageIdentifier() {
        return REVERSE_IMAGE;
    }

    @Override
    public int getStartYear() {return START_YEAR;}

    private static final Integer START_YEAR = 1851;
    private static final Integer STOP_YEAR = 1889;

    @Override
    public int getStopYear() {return STOP_YEAR;}

    @Override
    public int getCoinSlotImage(CoinSlot coinSlot, boolean ignoreImageId) {
        Integer slotImage;
        Integer imageId = coinSlot.getImageId();
        if (!ignoreImageId && (imageId >= 0 && imageId < COIN_IMG_IDS.length)) {
            slotImage = (Integer) COIN_IMG_IDS[imageId][1];
        } else {
            slotImage = COIN_MAP.get(coinSlot.getIdentifier());
        }
        return (slotImage != null) ? slotImage : (int) COIN_IDENTIFIERS[0][1];
    }

    @Override
    public Object[][] getImageIds() {return COIN_IMG_IDS;}


    @Override
    public void getCreationParameters(HashMap<String, Object> parameters) {
        parameters.put(CoinPageCreator.OPT_EDIT_DATE_RANGE, Boolean.FALSE);
        parameters.put(CoinPageCreator.OPT_START_YEAR, START_YEAR);
        parameters.put(CoinPageCreator.OPT_STOP_YEAR, STOP_YEAR);

        parameters.put(CoinPageCreator.OPT_CHECKBOX_1, Boolean.TRUE);
        parameters.put(CoinPageCreator.OPT_CHECKBOX_1_STRING_ID, R.string.include_silver);

        parameters.put(CoinPageCreator.OPT_CHECKBOX_2, Boolean.TRUE);
        parameters.put(CoinPageCreator.OPT_CHECKBOX_2_STRING_ID, R.string.include_nickel);
    }

    @Override
    public void populateCollectionLists(HashMap<String, Object> parameters, ArrayList<CoinSlot> coinList) {
        Integer startYear = (Integer) parameters.get(CoinPageCreator.OPT_START_YEAR);
        Integer stopYear = (Integer) parameters.get(CoinPageCreator.OPT_STOP_YEAR);
        Boolean show_silver = (Boolean) parameters.get(CoinPageCreator.OPT_CHECKBOX_1);
        Boolean show_nickel = (Boolean) parameters.get(CoinPageCreator.OPT_CHECKBOX_2);

        int coinIndex = 0;

        for (Integer i = startYear; i <= stopYear; i++) {
            String year = Integer.toString(i);
            if (show_silver) {
                if (i == 1851) {
                    coinList.add(new CoinSlot(year,String.format("%nSilver"), coinIndex++, getImgId("Silver")));
                    coinList.add(new CoinSlot(year,String.format("O %nSilver") , coinIndex++,getImgId("Silver") ));
                }
                if (i > 1851 && i < 1873) {
                    coinList.add(new CoinSlot(year,String.format("%nSilver"), coinIndex++, getImgId("Silver")));}
                if (i == 1873) {
                    coinList.add(new CoinSlot(year,String.format("%nSilver Proof"), coinIndex++, getImgId("Silver")));}
            }
            if (show_nickel) {
                if(i>1864 && i<1890 && i!=1877 && i!=1878 && i!=1886) {
                    coinList.add(new CoinSlot(year,String.format("%nNickel"),  coinIndex++,getImgId("Nickel")));}
                if(i==1877 || i==1878 || i==1886){
                    coinList.add(new CoinSlot(year,String.format("%nNickel Proof"), coinIndex++,getImgId("Nickel")));}
            }
        }
    }
    private static final int ATTRIBUTION =R.string.attr_wikitrimes;
    @Override
    public int getAttributionResId() {return ATTRIBUTION;}

    @Override
    public int onCollectionDatabaseUpgrade(SQLiteDatabase db, CollectionListInfo collectionListInfo,
                                           int oldVersion, int newVersion) {return 0;}
}


