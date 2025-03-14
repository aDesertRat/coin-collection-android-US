package com.spencerpages.collections;

import android.database.sqlite.SQLiteDatabase;

import com.coincollection.CoinPageCreator;
import com.coincollection.CoinSlot;
import com.coincollection.CollectionInfo;
import com.coincollection.CollectionListInfo;
import com.spencerpages.R;

import java.util.ArrayList;
import java.util.HashMap;

public class HalfDimes extends CollectionInfo {

    public static final String COLLECTION_TYPE = "Half Dimes";


    private static final Object[][] COIN_IDENTIFIERS = {
            {"Flowing Hair", R.drawable.a1794_half_dime},
            {"Draped Bust", R.drawable.a1797drapeddime},
            {"Capped Bust", R.drawable.a1820cappeddime},
            {"Seated No Stars", R.drawable.anostarsdime},
            {"Seated Stars", R.drawable.astarsdime},
            {"Seated Arrows", R.drawable.astars_arrowsdime},
            {"Seated Legend", R.drawable.alegenddime},
    };

    private static final Object[][] COIN_IMG_IDS = {
            {"Flowing Hair", R.drawable.a1794_half_dime},      // 0
            {"Draped Bust", R.drawable.a1797drapeddime},       // 1
            {"Capped Bust", R.drawable.a1820cappeddime},       // 2
            {"Seated No Stars", R.drawable.anostarsdime},      // 3
            {"Seated Stars", R.drawable.astarsdime},           // 4
            {"Seated Arrows", R.drawable.astars_arrowsdime},   // 5
            {"Seated Legend", R.drawable.alegenddime},         // 6
    };

    private static final HashMap<String, Integer> COIN_MAP = new HashMap<>();

    static {
        // Populate the COIN_MAP HashMap for quick image ID lookups later
        for (Object[] coinData : COIN_IDENTIFIERS) {COIN_MAP.put((String) coinData[0], (Integer) coinData[1]);}
    }

    private static final Integer START_YEAR = 1794;
    private static final Integer STOP_YEAR = 1873;

    private static final int REVERSE_IMAGE = R.drawable.astarsdime;

    @Override
    public String getCoinType() {return COLLECTION_TYPE;}

    @Override
    public int getCoinImageIdentifier() {return REVERSE_IMAGE;}

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
        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, Boolean.TRUE);

        parameters.put(CoinPageCreator.OPT_CHECKBOX_1, Boolean.TRUE);
        parameters.put(CoinPageCreator.OPT_CHECKBOX_1_STRING_ID, R.string.include_bust);

        parameters.put(CoinPageCreator.OPT_CHECKBOX_2, Boolean.TRUE);
        parameters.put(CoinPageCreator.OPT_CHECKBOX_2_STRING_ID, R.string.include_seated);



        // Use the MINT_MARK_1 checkbox for whether to include 'P' coins
        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, Boolean.TRUE);
        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1_STRING_ID, R.string.include_p);

        // Use the MINT_MARK_2 checkbox for whether to include 'O' coins
        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, Boolean.TRUE);
        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2_STRING_ID, R.string.include_o);

        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, Boolean.TRUE);
        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3_STRING_ID, R.string .include_s);
    }

    public void populateCollectionLists(HashMap<String, Object> parameters, ArrayList<CoinSlot> coinList) {
        Integer startYear = (Integer) parameters.get(CoinPageCreator.OPT_START_YEAR);
        Integer stopYear = (Integer) parameters.get(CoinPageCreator.OPT_STOP_YEAR);
        Boolean showbust = (Boolean) parameters.get(CoinPageCreator.OPT_CHECKBOX_1);
        Boolean showseated = (Boolean) parameters.get(CoinPageCreator.OPT_CHECKBOX_2);
        Boolean showP = (Boolean) parameters.get(CoinPageCreator.OPT_SHOW_MINT_MARK_1);
        Boolean showo = (Boolean) parameters.get(CoinPageCreator.OPT_SHOW_MINT_MARK_2);
        Boolean showS = (Boolean) parameters.get(CoinPageCreator.OPT_SHOW_MINT_MARK_3);

        int coinIndex = 0;


        for (Integer i = startYear; i <= stopYear; i++) {
            if (showbust && i == 1794 ) {
                coinList.add(new CoinSlot(Integer.toString(i),"Flowing Hair", coinIndex++,0));}
            if (showbust && i == 1795) {
                coinList.add(new CoinSlot(Integer.toString(i),"Flowing Hair", coinIndex++,0));}
            if (showbust && i > 1795 && i < 1798) {
                coinList.add(new CoinSlot(Integer.toString(i),"Draped Bust Small Eagle", coinIndex++,1));}
            if (showbust && i > 1799 && i < 1806 && i != 1804) {
                coinList.add(new CoinSlot(Integer.toString(i),"Draped Bust Heraldic Eagle", coinIndex++,1));}
            if (showbust && i > 1828 && i < 1838) {
                coinList.add(new CoinSlot(Integer.toString(i),"Capped Bust", coinIndex++,2));}

            if (showseated) {
                if (showP) {
                    if (i == 1837) {
                        coinList.add(new CoinSlot(Integer.toString(i),"No Stars Sm Date", coinIndex++,3));}
                    if (i == 1837) {
                        coinList.add(new CoinSlot(Integer.toString(i),"No Stars Lg Date", coinIndex++,3));}
                    if (i > 1837 && i < 1841) {
                        coinList.add(new CoinSlot(Integer.toString(i),"Stars No Drapery", coinIndex++,4));}
                    if (i > 1839 && i < 1860 && i != 1854 && i != 1855) {
                        coinList.add(new CoinSlot(Integer.toString(i),"Stars", coinIndex++,4));}
                    if (i == 1848) {
                        coinList.add(new CoinSlot(Integer.toString(i),"Stars Lg Date", coinIndex++,4));}
                    if (i == 1849) {
                        coinList.add(new CoinSlot(Integer.toString(i),"Stars 9 Over 6", coinIndex++,4));}
                    if (i == 1853 || i == 1854 || i == 1855) {
                        coinList.add(new CoinSlot(Integer.toString(i),"Arrows", coinIndex++,5));}
                    if (i == 1858) {
                        coinList.add(new CoinSlot(Integer.toString(i),"Stars Double Date", coinIndex++,4));
                        coinList.add(new CoinSlot(Integer.toString(i),"Stars Inverted Date", coinIndex++,4));}
                    if (i > 1859) {
                        coinList.add(new CoinSlot(Integer.toString(i),"Legend", coinIndex++,6));}
                    if (i == 1861) {
                        coinList.add(new CoinSlot(Integer.toString(i),"Legend 1 Over 0", coinIndex++,6));}
                }
                if (showo) {
                    if (i == 1838) {
                        coinList.add(new CoinSlot(Integer.toString(i),"O No Stars", coinIndex++,3));}
                    if (i == 1839 || i == 1840) {
                        coinList.add(new CoinSlot(Integer.toString(i),"O Stars No Drapery", coinIndex++,4));}
                    if (i > 1839 && i < 1861 && i != 1843 && i != 1845 && i != 1846 && i != 1847 && i != 1854 && i != 1855) {
                        coinList.add(new CoinSlot(Integer.toString(i),"O Stars", coinIndex++,4));}
                    if (i == 1853 || i == 1854 || i == 1855) {
                        coinList.add(new CoinSlot(Integer.toString(i),"O Arrows", coinIndex++,5));}
                }
                if (showS) {
                    if (i > 1862 && i != 1870) {
                        coinList.add(new CoinSlot(Integer.toString(i),"S Legend", coinIndex++,6));}
                    if (i == 1870) {
                        coinList.add(new CoinSlot(Integer.toString(i),"S Legend One Known", coinIndex++,6));}
                    if (i == 1872) {
                        coinList.add(new CoinSlot(Integer.toString(i),"S Legend S Under Bow",coinIndex++,6));}
                }
            }
        }
    }
    @Override
    public int getAttributionResId () {
        return R.string.attr_wikihalfdimes;
    }

    @Override
    public int getStartYear () {
        return START_YEAR;
    }

    @Override
    public int getStopYear () {
        return STOP_YEAR;
    }

    @Override
    public int onCollectionDatabaseUpgrade (SQLiteDatabase db, CollectionListInfo
            collectionListInfo,
                                            int oldVersion, int newVersion){
        return 0;
    }
}








