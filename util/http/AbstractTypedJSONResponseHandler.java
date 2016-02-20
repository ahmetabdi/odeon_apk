package uk.co.odeon.androidapp.util.http;

public abstract class AbstractTypedJSONResponseHandler<Result> extends AbstractJSONResponseHandler implements TypedResponseHandler<Result> {
    protected Result result;

    public AbstractTypedJSONResponseHandler() {
        this.result = null;
    }

    public Result getResult() {
        return this.result;
    }

    protected void setResult(Result result) {
        this.result = result;
    }
}
