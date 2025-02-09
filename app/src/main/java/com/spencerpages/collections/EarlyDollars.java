package com.spencerpages.collections;

import android.database.sqlite.SQLiteDatabase;

import com.coincollection.CoinPageCreator;
import com.coincollection.CoinSlot;
import com.coincollection.CollectionInfo;
import com.coincollection.CollectionListInfo;
import com.spencerpages.R;

import java.util.ArrayList;
import java.util.HashMap;

public class EarlyDollars extends CollectionInfo {

    public static final String COLLECTION_TYPE = "Early Dollars";


    private static final Object[][] COIN_IDENTIFIERS = {
            {"Flowing Hair", R.drawable.a1795_half_dollar_obv},
            {"Draped Bust", R.drawable.a1796_half_dollar_obverse_15_stars},
            {"Seated", R.drawable.a1885_half_dollar_obv},
            {"Seated ", R.drawable.anostarsdime},
            {"Trade", R.drawable.annc1884_t_1_trade_dollar__judd_1732_},
    };


    private static final HashMap<String, Integer> COIN_MAP = new HashMap<>();

    static {
        // Populate the COIN_MAP HashMap for quick image ID lookups later
        for (Object[] coinData : COIN_IDENTIFIERS) {
            COIN_MAP.put((String) coinData[0], (Integer) coinData[1]);
        }

    }


    private static final Integer START_YEAR = 1794;
    private static final Integer STOP_YEAR = 1885;

    private static final int OBVERSE_IMAGE_COLLECTED = R.drawable.annc1884_t_1_trade_dollar__judd_1732_;

    private static final int REVERSE_IMAGE = R.drawable.annc1884_t_1_trade_dollar__judd_1732_;

    @Override
    public String getCoinType() {
        return COLLECTION_TYPE;
    }

    @Override
    public int getCoinImageIdentifier() {
        return REVERSE_IMAGE;
    }


    public int getCoinSlotImage(CoinSlot coinSlot, boolean ignoreImageId) {
        Integer slotImage = COIN_MAP.get(coinSlot.getIdentifier());
        return (slotImage != null) ? slotImage : OBVERSE_IMAGE_COLLECTED;
    }

    @Override
    public void getCreationParameters(HashMap<String, Object> parameters) {

        parameters.put(CoinPageCreator.OPT_EDIT_DATE_RANGE, Boolean.FALSE);
        parameters.put(CoinPageCreator.OPT_START_YEAR, START_YEAR);
        parameters.put(CoinPageCreator.OPT_STOP_YEAR, STOP_YEAR);
        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARKS, Boolean.TRUE);

        parameters.put(CoinPageCreator.OPT_CHECKBOX_1, Boolean.FALSE);
        parameters.put(CoinPageCreator.OPT_CHECKBOX_1_STRING_ID, R.string.include_bust);

        parameters.put(CoinPageCreator.OPT_CHECKBOX_2, Boolean.FALSE);
        parameters.put(CoinPageCreator.OPT_CHECKBOX_2_STRING_ID, R.string.include_seated);

        parameters.put(CoinPageCreator.OPT_CHECKBOX_3, Boolean.TRUE);
        parameters.put(CoinPageCreator.OPT_CHECKBOX_3_STRING_ID, R.string.include_trade);

        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1, Boolean.TRUE);
        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_1_STRING_ID, R.string.include_p);

        // Use the MINT_MARK_1 checkbox for whether to include 'P' coins
        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2, Boolean.TRUE);
        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_2_STRING_ID, R.string.include_o);

        // Use the MINT_MARK_2 checkbox for whether to include 'O' coins
        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3, Boolean.TRUE);
        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_3_STRING_ID, R.string.include_s);

        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_4, Boolean.TRUE);
        parameters.put(CoinPageCreator.OPT_SHOW_MINT_MARK_4_STRING_ID, R.string.include_cc);

    }

    public void populateCollectionLists(HashMap<String, Object> parameters, ArrayList<CoinSlot> coinList) {
        Integer startYear = (Integer) parameters.get(CoinPageCreator.OPT_START_YEAR);
        Integer stopYear = (Integer) parameters.get(CoinPageCreator.OPT_STOP_YEAR);
        Boolean showbust = (Boolean) parameters.get(CoinPageCreator.OPT_CHECKBOX_1);
        Boolean showseated = (Boolean) parameters.get(CoinPageCreator.OPT_CHECKBOX_2);
        Boolean showtrade = (Boolean) parameters.get(CoinPageCreator.OPT_CHECKBOX_3);
        Boolean showP = (Boolean) parameters.get(CoinPageCreator.OPT_SHOW_MINT_MARK_1);
        Boolean showO = (Boolean) parameters.get(CoinPageCreator.OPT_SHOW_MINT_MARK_2);
        Boolean showS = (Boolean) parameters.get(CoinPageCreator.OPT_SHOW_MINT_MARK_3);
        Boolean showCC = (Boolean) parameters.get(CoinPageCreator.OPT_SHOW_MINT_MARK_4);

        int coinIndex = 0;


        for (Integer i = startYear; i <= stopYear; i++) {
            if(showbust){
                if(i==1794 || i==1795){coinList.add(new CoinSlot("Flowing Hair", String.format("%d", i), coinIndex++));}
                if(i>1794 && i<1799){coinList.add(new CoinSlot("Draped Bust", String.format("%d Sm Eagle", i), coinIndex++));}
                if(i>1797 && i<1804){coinList.add(new CoinSlot("Draped Bust", String.format("%d Heraldic Eagle", i), coinIndex++));}
                if(i==1804){coinList.add(new CoinSlot("Draped Bust", String.format("%d Rare", i), coinIndex++));}
            }
            if(showseated){
                if (showP) {
                    if (i == 1836) {coinList.add(new CoinSlot("Seated ", String.format("%d Gobrecht", i), coinIndex++));}
                    if (i == 1838 || i == 1839) {coinList.add(new CoinSlot("Seated", String.format("%d Gobrecht Proof", i), coinIndex++));}
                    if (i > 1839 && i < 1866 && i != 1858) {coinList.add(new CoinSlot("Seated", String.format("%d", i), coinIndex++));}
                    if (i > 1865 && i < 1874) {coinList.add(new CoinSlot("Seated", String.format("%d Motto", i), coinIndex++));}
                }
                if(showO){
                    if(i==1846 || i==1850 || i==1851 || i==1859 || i==1860){coinList.add(new CoinSlot("Seated", String.format("%d O", i), coinIndex++));}
                }
                if(showS){
                    if( i==1859 || i==1870 || i==1872 || i == 1873){coinList.add(new CoinSlot("Seated", String.format("%d S", i), coinIndex++));}
                }
                if(showCC && i>1869 && i<1874){coinList.add(new CoinSlot("Seated", String.format("%d CC", i), coinIndex++));}
            }
            if(showtrade){
                if (showP) {
                    if (i > 1872 && i < 1878) {coinList.add(new CoinSlot("Trade", String.format("%d", i), coinIndex++));}
                    if(i>1878 && i <1886){coinList.add(new CoinSlot("Trade", String.format("%d Proof", i), coinIndex++));}
                }
                if(showS && i>1872 && i<1879){coinList.add(new CoinSlot("Trade", String.format("%d S", i), coinIndex++));}
                if(showCC && i>1872 && i<1879){coinList.add(new CoinSlot("Trade", String.format("%d CC", i), coinIndex++));}
            }
        }
    }

    @Override
    public int getAttributionResId() {
        return R.string.attr_EarlyHalfs;
    }

    @Override
    public int getStartYear() {
        return START_YEAR;
    }

    @Override
    public int getStopYear() {
        return STOP_YEAR;
    }

    @Override
    public int onCollectionDatabaseUpgrade(SQLiteDatabase db, CollectionListInfo collectionListInfo,
                                           int oldVersion, int newVersion) {
        return 0;
    }
}
