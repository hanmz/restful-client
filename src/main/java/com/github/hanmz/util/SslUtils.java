package com.github.hanmz.util;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * *
 * Created by hanmz on 2017/10/8.
 */
public class SslUtils {
  /**
   * 忽略https证书
   */
  public static SSLSocketFactory defaultSSLSocketFactory() {
    SSLSocketFactory sSLSocketFactory = null;
    try {
      SSLContext sc = SSLContext.getInstance("TLS");
      sc.init(null, new TrustManager[] {new TrustAllManager()}, new SecureRandom());
      sSLSocketFactory = sc.getSocketFactory();
    } catch (Exception e) {
      //
    }
    return sSLSocketFactory;
  }

  /**
   * 设置证书
   */
  public static SSLSocketFactory createSSLSocketFactory(InputStream certificate) {
    SSLSocketFactory sSLSocketFactory = null;
    try {
      CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
      KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      keyStore.load(null);
      String certificateAlias = Integer.toString(0);
      keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

      final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(keyStore);

      SSLContext sc = SSLContext.getInstance("TLS");
      sc.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
      sSLSocketFactory = sc.getSocketFactory();
    } catch (Exception e) {
      //
    }
    return sSLSocketFactory;
  }


  /**
   * 忽略https证书
   */
  private static class TrustAllManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)

      throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      X509Certificate[] x509Certificates = new X509Certificate[0];
      return x509Certificates;
    }
  }
}
