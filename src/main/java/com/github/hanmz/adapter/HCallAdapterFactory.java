package com.github.hanmz.adapter;

import com.github.hanmz.util.InnerUtils;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

/**
 * *
 * Created by hanmz on 2017/9/10.
 */
public class HCallAdapterFactory extends CallAdapter.Factory {
  private HCallAdapterFactory() {
  }

  public static HCallAdapterFactory create() {
    return new HCallAdapterFactory();
  }

  @Nullable
  @Override
  public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
    return InnerUtils.isAsync(returnType) ? new AsyncCallAdapter(returnType) : new DefaultCallAdapter<>(returnType);
  }

  /**
   * 同步
   */
  private static final class DefaultCallAdapter<R> implements CallAdapter<R, Call<?>> {

    Type returnType;

    DefaultCallAdapter(Type returnType) {
      this.returnType = returnType;
    }

    @Override
    public Type responseType() {
      return returnType;
    }

    @Override
    public Call<R> adapt(Call<R> call) {
      return call;
    }
  }


  /**
   * 异步
   */
  private static final class AsyncCallAdapter<R> implements CallAdapter<R, CompletableFuture<R>> {
    private final Type responseType;

    AsyncCallAdapter(Type responseType) {
      this.responseType = responseType;
    }

    @Override
    public Type responseType() {
      return InnerUtils.getType(responseType);
    }

    /**
     * @param call
     * @return
     */
    @Override
    public CompletableFuture<R> adapt(final Call<R> call) {
      final CompletableFuture<R> future = new CompletableFuture<R>() {
        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
          if (mayInterruptIfRunning) {
            call.cancel();
          }
          return super.cancel(mayInterruptIfRunning);
        }
      };

      call.enqueue(new Callback<R>() {
        @Override
        public void onResponse(Call<R> call, Response<R> response) {
          future.complete(response.body());
        }

        @Override
        public void onFailure(Call<R> call, Throwable t) {
          future.completeExceptionally(t);
        }
      });

      return future;
    }
  }
}
