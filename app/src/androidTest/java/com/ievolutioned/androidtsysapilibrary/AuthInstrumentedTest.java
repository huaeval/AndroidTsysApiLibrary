package com.ievolutioned.androidtsysapilibrary;

import android.support.test.runner.AndroidJUnit4;

import com.ievolutioned.tsysapilibrary.TsysApiLibrary;
import com.ievolutioned.tsysapilibrary.transit.BaseResponse;
import com.ievolutioned.tsysapilibrary.transit.CardDataSources;
import com.ievolutioned.tsysapilibrary.transit.ErrorResponse;
import com.ievolutioned.tsysapilibrary.transit.TransitServiceCallback;
import com.ievolutioned.tsysapilibrary.transit.cardservices.AuthService;
import com.ievolutioned.tsysapilibrary.transit.model.Auth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AuthInstrumentedTest {

    private AuthService.AuthResponse authResponse = null;
    private ErrorResponse errorResponse = null;
    private CountDownLatch delay = new CountDownLatch(1);

    private static String deviceId = Util.DEVICE_ID;
    private static String transactionKey = Util.TRANSACTION_KEY;

    @Before
    public void setUp() throws Exception {
        delay.await(6, TimeUnit.SECONDS);
        while (delay.getCount() > 0) {
            delay.countDown();
        }
    }

    @Test
    public void testThatAuthServicePasses() throws Exception {
        authResponse = null;
        errorResponse = null;
        String cardDataSource = CardDataSources.MANUAL;
        String transactionAmount = "0.10";
        String cardNumber = Util.CARD_NUMBER;
        String expirationDate = "0819";

        Auth auth = new Auth(deviceId, transactionKey, cardDataSource, transactionAmount, cardNumber, expirationDate);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TsysApiLibrary.doAuthorization(auth, new TransitServiceCallback() {
            @Override
            public void onSuccess(String msg, BaseResponse response) {
                authResponse = (AuthService.AuthResponse) response;
                countDownLatch.countDown();
            }

            @Override
            public void onError(String msg, BaseResponse response) {
                if (response instanceof AuthService.AuthResponse)
                    authResponse = (AuthService.AuthResponse) response;
                else if (response instanceof ErrorResponse)
                    errorResponse = (ErrorResponse) response;
                countDownLatch.countDown();
            }

            @Override
            public void onCancel() {
                countDownLatch.countDown();
            }
        });

        while (countDownLatch.getCount() > 0) {
            countDownLatch.await(1, TimeUnit.SECONDS);
        }
        assertTrue(authResponse.getStatus().contentEquals(AuthService.AuthResponse.PASS));
        assertTrue(authResponse.getResponseCode().contentEquals("A0000"));
    }

    @Test
    public void testThatWrongExpirationDateFails() throws Exception {
        authResponse = null;
        errorResponse = null;
        String cardDataSource = CardDataSources.MANUAL;
        String transactionAmount = "0.10";
        String cardNumber = Util.CARD_NUMBER;
        //Wrong expirationDate
        String expirationDate = "xxxx";

        Auth auth = new Auth(deviceId, transactionKey, cardDataSource, transactionAmount, cardNumber, expirationDate);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TsysApiLibrary.doAuthorization(auth, new TransitServiceCallback() {
            @Override
            public void onSuccess(String msg, BaseResponse response) {

                countDownLatch.countDown();
            }

            @Override
            public void onError(String msg, BaseResponse response) {
                if (response instanceof AuthService.AuthResponse)
                    authResponse = (AuthService.AuthResponse) response;
                else if (response instanceof ErrorResponse)
                    errorResponse = (ErrorResponse) response;
                countDownLatch.countDown();
            }

            @Override
            public void onCancel() {
                countDownLatch.countDown();
            }
        });

        while (countDownLatch.getCount() > 0) {
            countDownLatch.await(1, TimeUnit.SECONDS);
        }
        assertTrue(authResponse.getStatus().contentEquals(AuthService.AuthResponse.FAIL));
        assertTrue(authResponse.getResponseCode().contentEquals("F9901"));

    }

    @Test
    public void testThatWrongCardNumberFails() throws Exception {
        authResponse = null;
        errorResponse = null;
        String cardDataSource = CardDataSources.MANUAL;
        String transactionAmount = "0.10";
        //Invalid cardNumber
        String cardNumber = Util.CARD_NUMBER+"xx";
        String expirationDate = "0819";

        Auth auth = new Auth(deviceId, transactionKey, cardDataSource, transactionAmount, cardNumber, expirationDate);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TsysApiLibrary.doAuthorization(auth, new TransitServiceCallback() {
            @Override
            public void onSuccess(String msg, BaseResponse response) {

                countDownLatch.countDown();
            }

            @Override
            public void onError(String msg, BaseResponse response) {
                if (response instanceof AuthService.AuthResponse)
                    authResponse = (AuthService.AuthResponse) response;
                else if (response instanceof ErrorResponse)
                    errorResponse = (ErrorResponse) response;
                countDownLatch.countDown();
            }

            @Override
            public void onCancel() {
                countDownLatch.countDown();
            }
        });

        while (countDownLatch.getCount() > 0) {
            countDownLatch.await(1, TimeUnit.SECONDS);
        }
        assertTrue(authResponse.getStatus().contentEquals(AuthService.AuthResponse.FAIL));
        assertTrue(authResponse.getResponseCode().contentEquals("E7260"));

    }

    @Test
    public void testThatNullCardNumberFails() throws Exception {
        authResponse = null;
        errorResponse = null;
        String cardDataSource = CardDataSources.MANUAL;
        String transactionAmount = "0.10";
        //Null cardNumber, empty cardNumber
        String cardNumber = "";
        String expirationDate = "0819";

        Auth auth = new Auth(deviceId, transactionKey, cardDataSource, transactionAmount, cardNumber, expirationDate);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TsysApiLibrary.doAuthorization(auth, new TransitServiceCallback() {
            @Override
            public void onSuccess(String msg, BaseResponse response) {
                authResponse = (AuthService.AuthResponse) response;
                countDownLatch.countDown();
            }

            @Override
            public void onError(String msg, BaseResponse response) {
                if (response instanceof AuthService.AuthResponse)
                    authResponse = (AuthService.AuthResponse) response;
                else if (response instanceof ErrorResponse)
                    errorResponse = (ErrorResponse) response;
                countDownLatch.countDown();
            }

            @Override
            public void onCancel() {
                countDownLatch.countDown();
            }
        });

        while (countDownLatch.getCount() > 0) {
            countDownLatch.await(1, TimeUnit.SECONDS);
        }
        assertTrue(authResponse == null);
        assertTrue(errorResponse.getMsg().contains(" must not be empty or null"));
    }

    @Test
    public void testThatNullExpirationDateFails() throws Exception {
        authResponse = null;
        errorResponse = null;
        String cardDataSource = CardDataSources.MANUAL;
        String transactionAmount = "0.10";
        String cardNumber = Util.CARD_NUMBER;
        //null expirationDate, empty expirationDate
        String expirationDate = "";

        Auth auth = new Auth(deviceId, transactionKey, cardDataSource, transactionAmount, cardNumber, expirationDate);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TsysApiLibrary.doAuthorization(auth, new TransitServiceCallback() {
            @Override
            public void onSuccess(String msg, BaseResponse response) {
                authResponse = (AuthService.AuthResponse) response;
                countDownLatch.countDown();
            }

            @Override
            public void onError(String msg, BaseResponse response) {
                if (response instanceof AuthService.AuthResponse)
                    authResponse = (AuthService.AuthResponse) response;
                else if (response instanceof ErrorResponse)
                    errorResponse = (ErrorResponse) response;
                countDownLatch.countDown();
            }

            @Override
            public void onCancel() {
                countDownLatch.countDown();
            }
        });

        while (countDownLatch.getCount() > 0) {
            countDownLatch.await(1, TimeUnit.SECONDS);
        }
        assertTrue(authResponse == null);
        assertTrue(errorResponse.getMsg().contains("must not be empty or null"));
    }


    @Test
    public void testThatTransactionAmountIsZero() throws Exception {
        authResponse = null;
        errorResponse = null;
        String cardDataSource = CardDataSources.MANUAL;
        //transactionAmount equals to 0.00
        String transactionAmount = "0.00";
        String cardNumber = Util.CARD_NUMBER;
        String expirationDate = "0819";

        Auth auth = new Auth(deviceId, transactionKey, cardDataSource, transactionAmount, cardNumber, expirationDate);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TsysApiLibrary.doAuthorization(auth, new TransitServiceCallback() {
            @Override
            public void onSuccess(String msg, BaseResponse response) {

                countDownLatch.countDown();
            }

            @Override
            public void onError(String msg, BaseResponse response) {
                if (response instanceof AuthService.AuthResponse)
                    authResponse = (AuthService.AuthResponse) response;
                else if (response instanceof ErrorResponse)
                    errorResponse = (ErrorResponse) response;
                countDownLatch.countDown();
            }

            @Override
            public void onCancel() {
                countDownLatch.countDown();
            }
        });

        while (countDownLatch.getCount() > 0) {
            countDownLatch.await(1, TimeUnit.SECONDS);
        }
        assertTrue(authResponse.getStatus().contentEquals(AuthService.AuthResponse.FAIL));
        assertTrue(authResponse.getResponseCode().contentEquals("D2999"));

    }

    @Test
    public void testTransactionAmountNegativeFails() throws Exception {
        authResponse = null;
        errorResponse = null;
        String cardDataSource = CardDataSources.MANUAL;
        //invalid transactionAmount
        String transactionAmount = "-0.10";
        String cardNumber = Util.CARD_NUMBER;
        String expirationDate = "0819";

        Auth auth = new Auth(deviceId, transactionKey, cardDataSource, transactionAmount, cardNumber, expirationDate);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TsysApiLibrary.doAuthorization(auth, new TransitServiceCallback() {
            @Override
            public void onSuccess(String msg, BaseResponse response) {

                countDownLatch.countDown();
            }

            @Override
            public void onError(String msg, BaseResponse response) {
                if (response instanceof AuthService.AuthResponse)
                    authResponse = (AuthService.AuthResponse) response;
                else if (response instanceof ErrorResponse)
                    errorResponse = (ErrorResponse) response;
                countDownLatch.countDown();
            }

            @Override
            public void onCancel() {
                countDownLatch.countDown();
            }
        });

        while (countDownLatch.getCount() > 0) {
            countDownLatch.await(1, TimeUnit.SECONDS);
        }
        assertTrue(authResponse.getStatus().contentEquals(AuthService.AuthResponse.FAIL));
        assertTrue(authResponse.getResponseCode().contentEquals("F9901"));

    }

    @Test
    public void testThatTransactionAmountIsTheSameInResponse() throws Exception {
        authResponse = null;
        errorResponse = null;
        String cardDataSource = CardDataSources.MANUAL;
        //invalid transactionAmount
        String transactionAmount = "0.10";
        String cardNumber = Util.CARD_NUMBER;
        String expirationDate = "0819";

        Auth auth = new Auth(deviceId, transactionKey, cardDataSource, transactionAmount, cardNumber, expirationDate);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TsysApiLibrary.doAuthorization(auth, new TransitServiceCallback() {
            @Override
            public void onSuccess(String msg, BaseResponse response) {
                authResponse = (AuthService.AuthResponse) response;
                countDownLatch.countDown();
            }

            @Override
            public void onError(String msg, BaseResponse response) {
                if (response instanceof AuthService.AuthResponse)
                    authResponse = (AuthService.AuthResponse) response;
                else if (response instanceof ErrorResponse)
                    errorResponse = (ErrorResponse) response;
                countDownLatch.countDown();
            }

            @Override
            public void onCancel() {
                countDownLatch.countDown();
            }
        });

        while (countDownLatch.getCount() > 0) {
            countDownLatch.await(1, TimeUnit.SECONDS);
        }
        assertTrue(authResponse.getStatus().contentEquals(AuthService.AuthResponse.PASS));
        assertTrue(authResponse.getTransactionAmount().equals(transactionAmount));
    }

    @Test
    public void testThatEmptyTransactionAmountFails() throws Exception {

        authResponse = null;
        errorResponse = null;
        String cardDataSource = CardDataSources.MANUAL;
        //empty transactionAmount
        String transactionAmount = "";
        String cardNumber = Util.CARD_NUMBER;
        String expirationDate = "0819";

        Auth auth = new Auth(deviceId, transactionKey, cardDataSource, transactionAmount, cardNumber, expirationDate);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TsysApiLibrary.doAuthorization(auth, new TransitServiceCallback() {
            @Override
            public void onSuccess(String msg, BaseResponse response) {
                authResponse = (AuthService.AuthResponse) response;
                countDownLatch.countDown();
            }

            @Override
            public void onError(String msg, BaseResponse response) {
                if (response instanceof AuthService.AuthResponse)
                    authResponse = (AuthService.AuthResponse) response;
                else if (response instanceof ErrorResponse)
                    errorResponse = (ErrorResponse) response;
                countDownLatch.countDown();
            }

            @Override
            public void onCancel() {
                countDownLatch.countDown();
            }
        });

        while (countDownLatch.getCount() > 0) {
            countDownLatch.await(1, TimeUnit.SECONDS);
        }
        assertTrue(authResponse == null);
        assertTrue(errorResponse.getMsg().contains("must not"));
    }
}
