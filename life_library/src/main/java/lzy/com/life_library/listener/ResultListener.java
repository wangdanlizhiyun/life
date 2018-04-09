package lzy.com.life_library.listener;

/**
 * Created by lizhiyun on 2018/1/17.
 */

public class ResultListener {
    ResultOkListener resultOkListener;
    ResultCancelListener resultCancelListener;
    ResultFirstUserListener resultFirstUserListener;

    public ResultOkListener getResultOkListener() {
        return resultOkListener;
    }

    public void setResultOkListener(ResultOkListener resultOkListener) {
        this.resultOkListener = resultOkListener;
    }

    public ResultCancelListener getResultCancelListener() {
        return resultCancelListener;
    }

    public void setResultCancelListener(ResultCancelListener resultCancelListener) {
        this.resultCancelListener = resultCancelListener;
    }

    public ResultFirstUserListener getResultFirstUserListener() {
        return resultFirstUserListener;
    }

    public void setResultFirstUserListener(ResultFirstUserListener resultFirstUserListener) {
        this.resultFirstUserListener = resultFirstUserListener;
    }

}
