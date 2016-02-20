package uk.co.odeon.androidapp.util.http;

public interface TypedResponseHandler<Result> extends ResponseHandler {
    Result getResult();
}
