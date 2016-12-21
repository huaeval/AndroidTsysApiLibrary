package com.ievolutioned.tsysapilibrary.transit.cardservices;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.ievolutioned.tsysapilibrary.net.NetUtil;
import com.ievolutioned.tsysapilibrary.transit.BaseResponse;
import com.ievolutioned.tsysapilibrary.transit.ErrorResponse;
import com.ievolutioned.tsysapilibrary.transit.TransitBase;
import com.ievolutioned.tsysapilibrary.transit.TransitServiceBase;
import com.ievolutioned.tsysapilibrary.transit.TransitServiceCallback;
import com.ievolutioned.tsysapilibrary.transit.model.TipAdjustment;
import com.ievolutioned.tsysapilibrary.util.JsonUtil;
import com.ievolutioned.tsysapilibrary.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * {@link TipAdjustmentService} service class. Allows to call the service of tip adjustment
 * and gets a response
 * <p>
 * Created by Daniel on 07/12/2016.
 * </p>
 */

public class TipAdjustmentService extends TransitServiceBase {

    private String TAG = SaleService.class.getName();

    public String URL = BASE_URL + "TipAdjustment";

    /**
     * {@link TipAdjustmentService} service task builder.
     * <p>
     * It doesn't execute the code, use @see TransitBase#execute()
     * </p>
     *
     * @param tipAdjustment - {@link TipAdjustment} object.
     * @param callback      - {@link TransitServiceCallback} callback.
     */
    public TipAdjustmentService(@NonNull final TipAdjustment tipAdjustment,
                                @NonNull final TransitServiceCallback callback) {
        task = new AsyncTask<Void, Void, BaseResponse>() {
            @Override
            protected BaseResponse doInBackground(Void... voids) {
                if (!isCancelled())
                    return callService(tipAdjustment);
                return null;
            }

            @Override
            protected void onPostExecute(BaseResponse baseResponse) {
                super.onPostExecute(baseResponse);
                if (!isCancelled())
                    handleResponse(baseResponse, callback);
                else
                    callback.onCancel();
            }
        };
    }

    @Override
    protected BaseResponse callService(TransitBase baseResponse) {
        TipAdjustment tipAdjustment = (TipAdjustment) baseResponse;
        try {
            JSONObject j = tipAdjustment.serialize();
            fields = new String[]{TipAdjustment.DEVICE_ID, TipAdjustment.TRANSACTION_KEY,
                    TipAdjustment.TIP, TipAdjustment.TRANSACTION_ID};
            tipAdjustment.validateEmptyNullFields(fields);
            String response = NetUtil.post(URL, j.toString(),
                    NetUtil.CONTENT_TYPE_JSON);
            JSONObject json = new JSONObject(response);
            JSONObject tipResponse = json.getJSONObject("TipAdjustmentResponse");
            return new TipAdjustmentResponse(tipResponse);
        } catch (JSONException je) {
            LogUtil.e(TAG, je.getMessage(), je);
            return null;
        } catch (IllegalArgumentException ia) {
            try {
                return new ErrorResponse(ia.getMessage(), ia);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
            return null;
        }
    }


    /**
     * {@link TipAdjustmentResponse} response class. Contains the specific attributes for tip
     * adjustment response from service
     */
    public class TipAdjustmentResponse extends BaseResponse {
        private String TOTAL_AMOUNT = "totalAmount";
        private String TIP = "tip";
        private String totalAmount;
        private String tip;

        private TipAdjustmentResponse(JSONObject jsonObject) throws JSONException {
            super(jsonObject);
            setTotalAmount(JsonUtil.getString(jsonObject, TOTAL_AMOUNT));
            setTip(JsonUtil.getString(jsonObject, TIP));
        }

        public String getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(String totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getTip() {
            return tip;
        }

        public void setTip(String tip) {
            this.tip = tip;
        }
    }

}
