package oneec.java.sample.models;

import java.math.BigDecimal;
import java.util.List;

public class MerchantCombinationProduct {
    public String itemNumber; // 貨號
    public List<MerchantCombinationInfo> combinationInfos; // 組合的內容資訊
    public String insertDt; // 格式 "yyyy-MM-ddTHH:mm:ss.SSSZ" ,UTC時間
    public String modifiedDt; // 格式 "yyyy-MM-ddTHH:mm:ss.SSSZ" ,UTC時間

    public void setMerchantCombinationInfo() {

    }
}
