package com.spencerpages.collections;

import android.database.sqlite.SQLiteDatabase;

import com.coincollection.CoinPageCreator;
import com.coincollection.CoinSlot;
import com.coincollection.CollectionInfo;
import com.coincollection.CollectionListInfo;
import com.spencerpages.R;

import java.util.ArrayList;
import java.util.HashMap;

public class EarlyDimes extends CollectionInfo {

    public static final String COLLECTION_TYPE = "Early Dimes";


    private static final Object[][] COIN_IDENTIFIERS = {
            {"Draped Bust", R.drawable.a1797drapeddime},
            {"Capped Bust", R.drawable.a1820cappeddime},
            {"Seated No Stars", R.drawable.anostarsdime},
            {"Seated Stars", R.drawable.astarsdime},
            {"Seated Arrows", R.drawable.astars_arrowsdime},
            {"Seated Legend", R.drawable.alegenddime},
            {"Seated Arrows ", R.drawable.alegendarrowsdime},
    };

    private static final Object[][] COIN_IMG_IDS = {
            {"Draped Bust", R.drawable.a1797drapeddime},                                     // 0
            {"Capped Bust", R.drawable.a1820cappeddime},                                     // 1
            {"Seated No Stars", R.drawable.anostarsdime},                                    // 2
            {"Seated Stars", R.drawable.astarsdime},                                         // 3
            {"Seated Stars&Arrows", R.drawable.astars_arrowsdime},                           // 4
            {"Seated Legend", R.drawable.alegenddime},                                       // 5
            {"Seated Legend&Arrows", R.drawable.alegendarrowsdime},                          // 6
            {"Barber", R.drawable.obv_barber_dime},                                          // 7
            {"Mercury", R.drawable.obv_mercury_dime},                                        // 8
            {"Roosevelt", R.drawable.obv_roosevelt_dime_unc},                                // 9
            {"1796 Draped Bust Sm Eagle Reverse", R.drawable.adi1796draped_bustr},           // 10
            {"1807 Draped Bust Heraldic Eagle Reverse", R.drawable.adi1807draped_bustr},     // 11
            {"1821 Capped Bust Reverse", R.drawable.adi1821r},                               // 12
            {"1838 Seated No Stars Reverse", R.drawable.adi1838r},                           // 13
            {"1843 Seated Stars Reverse", R.drawable.adi1843r},                              // 14
            {"1884 Legend Reverse", R.drawable.adi1884r},                                    // 15
            {"1914 Barber Reverse", R.drawable.adi1914r},                                    // 16
            {"1943 Mercury Reverse", R.drawable.adi1843r},                                   // 17
            {"2016 Roosevelt Reverse", R.drawable.adi2016r},                                 // 18
    };



    private static final HashMap<String, Integer> COIN_MAP = new HashMap<>();

    static {
        // Populate the COIN_MAP HashMap for quick image ID lookups later
        for (Object[] coinData : COIN_IDENTIFIERS) {COIN_MAP.put((String) coinData[0], (Integer) coinData[1]);}

    }


    private static final Integer START_YEAR = 1796;
    private static final Integer STOP_YEAR = 1891;

    private static final int REVERSE_IMAGE = R.drawable.anostarsdime;

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

        parameters.put(CoinPageCreator.OPT_CHECKBOX_1, Boolean.FALSE);
        parameters.put(CoinPageCreator.OPT_CHECKBOX_1_STRING_ID, R.string.include_old);

        parameters.put(CoinPageCreator.OPT_CHECKBOX_2, Boolean.FALSE);
        parameters.put(CoinPageCreator.OPT_CHECKBOX_2_STRING_ID, R.string.include_draped_bust);

        parameters.put(CoinPageCreator.OPT_CHECKBOX_3, Boolean.FALSE);
        parameters.put(CoinPageCreator.OPT_CHECKBOX_3_STRING_ID, R.string.include_capped_bust);

        parameters.put(CoinPageCreator.OPT_CHECKBOX_4, Boolean.TRUE);
        parameters.put(CoinPageCreator.OPT_CHECKBOX_4_STRING_ID, R.string.include_seated);



        // Use the MINT_MARK_1 checkbox for whether to include 'P' coins
        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, Boolean.TRUE);
        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1_STRING_ID, R.string.include_p);

        // Use the MINT_MARK_2 checkbox for whether to include 'O' coins
        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, Boolean.TRUE);
        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2_STRING_ID, R.string.include_o);

        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, Boolean.TRUE);
        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3_STRING_ID, R.string .include_s);

        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_4, Boolean.TRUE);
        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_4_STRING_ID, R.string .include_cc);
    }

    public void populateCollectionLists(HashMap<String, Object> parameters, ArrayList<CoinSlot> coinList) {
        Integer startYear = (Integer) parameters.get(CoinPageCreator.OPT_START_YEAR);
        Integer stopYear = (Integer) parameters.get(CoinPageCreator.OPT_STOP_YEAR);
        Boolean showOld = (Boolean) parameters.get(CoinPageCreator.OPT_CHECKBOX_1);
        Boolean showdraped = (Boolean) parameters.get(CoinPageCreator.OPT_CHECKBOX_2);
        Boolean showcapped = (Boolean) parameters.get(CoinPageCreator.OPT_CHECKBOX_3);
        Boolean showseated = (Boolean) parameters.get(CoinPageCreator.OPT_CHECKBOX_4);
        Boolean showP = (Boolean) parameters.get(CoinPageCreator.OPT_SHOW_MINT_MARK_1);
        Boolean showo = (Boolean) parameters.get(CoinPageCreator.OPT_SHOW_MINT_MARK_2);
        Boolean showS = (Boolean) parameters.get(CoinPageCreator.OPT_SHOW_MINT_MARK_3);
        Boolean showcc = (Boolean) parameters.get(CoinPageCreator.OPT_SHOW_MINT_MARK_4);

        int coinIndex = 0;


        if (showOld && !showdraped) {coinList.add(new CoinSlot("Draped Bust","", coinIndex++));}
        if (showOld && !showcapped) {coinList.add(new CoinSlot("Capped Bust","", coinIndex++));}

        for (Integer i = startYear; i <= stopYear; i++) {
            if (showdraped && i > 1795 && i < 1798) {
                coinList.add(new CoinSlot(Integer.toString(i),"Small Eagle", coinIndex++,0));}
            if (showdraped && i > 1797 && i < 1808 && i !=1799 && i != 1806) {
                coinList.add(new CoinSlot(Integer.toString(i),"Heraldic Eagle", coinIndex++,0));}
            if (showcapped && i==1809) {coinList.add(new CoinSlot(Integer.toString(i),"", coinIndex++,1));}
            if (showcapped && i==1811) {coinList.add(new CoinSlot(Integer.toString(i),"", coinIndex++,1));}
            if (showcapped && i==1814) {coinList.add(new CoinSlot(Integer.toString(i),"", coinIndex++,1));}
            if (showcapped && i>1819 && i<1838  && i!=1826) {coinList.add(new CoinSlot(Integer.toString(i),"", coinIndex++,1));}
            if (showseated){
                if (showP){
                    if ( i == 1837){coinList.add(new CoinSlot(Integer.toString(i),"No Stars", coinIndex++,2));}
                    if ( i>1837 && i<1860 && i!=1854 && i!= 1855 ){coinList.add(new CoinSlot(Integer.toString(i),"Stars", coinIndex++,3));}
                    if ( i == 1853 || i == 1854 || i== 1855){coinList.add(new CoinSlot(Integer.toString(i),"Arrows", coinIndex++,4));}
                    if( i > 1859 && i<1892 && i!=1874){coinList.add(new CoinSlot(Integer.toString(i),"Legend", coinIndex++,5));}
                    if( i==1873 || i==1874){coinList.add(new CoinSlot(Integer.toString(i),"Arrows", coinIndex++,6));}
                }
                if(showo && (i>1837 && i<1861 || i==1891) && i!=1844 && i!=1846 && i!=1847 && i !=1848 && i!=1855){
                    if ( i == 1838){coinList.add(new CoinSlot(Integer.toString(i),"O No Stars", coinIndex++,2));}
                    if ( (i > 1838 && i < 1860) && i != 1853 && i !=1854){coinList.add(new CoinSlot(Integer.toString(i),"O Stars", coinIndex++,3));}
                    if (  i == 1853 || i ==1854){coinList.add(new CoinSlot(Integer.toString(i),"O Arrows", coinIndex++,4));}
                    if (  i == 1860 || i ==1891){coinList.add(new CoinSlot(Integer.toString(i),"O Legend", coinIndex++,5));}
                }
                if (showS && (i>1855 && i<1892) && i!=1857 && i!=1878 && i!= 1879 && i!=1880 && i!=1881 && i!=1882 && i!=1883) {
                    if(i<1861){coinList.add(new CoinSlot(Integer.toString(i),"S Stars", coinIndex++,3));}
                    if ( i >1860 && i != 1873 && i != 1874){coinList.add(new CoinSlot(Integer.toString(i),"S Legend", coinIndex++,5));}
                    if ( i == 1873 || i ==1874){coinList.add(new CoinSlot(Integer.toString(i),"S Arrows", coinIndex++,6));}
                }
                if(showcc) {
                    if (i > 1870 && i < 1879 && i != 1873 && i != 1874) {
                        coinList.add(new CoinSlot(Integer.toString(i),"CC Legend", coinIndex++,5));}
                    if (i == 1873){
                        coinList.add(new CoinSlot(Integer.toString(i),"CC Legend", coinIndex++,5));
                        coinList.add(new CoinSlot(Integer.toString(i),"CC Arrows",  coinIndex++,6));}
                    if (i == 1874) {coinList.add(new CoinSlot(Integer.toString(i),"CC Arrows", coinIndex++,6));}
                }
            }


        }
    }
    @Override
    public int getAttributionResId() {return R.string.attr_wikidimes;}

    @Override
    public int getStartYear() {return START_YEAR;}

    @Override
    public int getStopYear() {return STOP_YEAR;}

    @Override
    public int onCollectionDatabaseUpgrade(SQLiteDatabase db, CollectionListInfo collectionListInfo,
                                           int oldVersion, int newVersion) {return 0;}
}







