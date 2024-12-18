package org.conscrypt;

import androidx.base.a.b;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
public final class ChainStrengthAnalyzer {
    private static final int MIN_DSA_P_LEN_BITS = 1024;
    private static final int MIN_DSA_Q_LEN_BITS = 160;
    private static final int MIN_EC_FIELD_SIZE_BITS = 160;
    private static final int MIN_RSA_MODULUS_LEN_BITS = 1024;
    private static final String[] SIGNATURE_ALGORITHM_OID_BLACKLIST = {"1.2.840.113549.1.1.2", "1.2.840.113549.1.1.3", "1.2.840.113549.1.1.4", "1.2.840.113549.1.1.5", "1.2.840.10040.4.3", "1.2.840.10045.4.1"};

    public static final void check(List<X509Certificate> list) {
        for (X509Certificate x509Certificate : list) {
            try {
                checkCert(x509Certificate);
            } catch (CertificateException e) {
                StringBuilder d = b.d("Unacceptable certificate: ");
                d.append(x509Certificate.getSubjectX500Principal());
                throw new CertificateException(d.toString(), e);
            }
        }
    }

    public static final void check(X509Certificate[] x509CertificateArr) {
        for (X509Certificate x509Certificate : x509CertificateArr) {
            try {
                checkCert(x509Certificate);
            } catch (CertificateException e) {
                StringBuilder d = b.d("Unacceptable certificate: ");
                d.append(x509Certificate.getSubjectX500Principal());
                throw new CertificateException(d.toString(), e);
            }
        }
    }

    public static final void checkCert(X509Certificate x509Certificate) {
        checkKeyLength(x509Certificate);
        checkSignatureAlgorithm(x509Certificate);
    }

    private static void checkKeyLength(X509Certificate x509Certificate) {
        PublicKey publicKey = x509Certificate.getPublicKey();
        if (publicKey instanceof RSAPublicKey) {
            if (((RSAPublicKey) publicKey).getModulus().bitLength() < 1024) {
                throw new CertificateException("RSA modulus is < 1024 bits");
            }
        } else if (publicKey instanceof ECPublicKey) {
            if (((ECPublicKey) publicKey).getParams().getCurve().getField().getFieldSize() < 160) {
                throw new CertificateException("EC key field size is < 160 bits");
            }
        } else if (!(publicKey instanceof DSAPublicKey)) {
            StringBuilder d = b.d("Rejecting unknown key class ");
            d.append(publicKey.getClass().getName());
            throw new CertificateException(d.toString());
        } else {
            DSAPublicKey dSAPublicKey = (DSAPublicKey) publicKey;
            int bitLength = dSAPublicKey.getParams().getP().bitLength();
            int bitLength2 = dSAPublicKey.getParams().getQ().bitLength();
            if (bitLength < 1024 || bitLength2 < 160) {
                throw new CertificateException("DSA key length is < (1024, 160) bits");
            }
        }
    }

    private static void checkSignatureAlgorithm(X509Certificate x509Certificate) {
        String sigAlgOID = x509Certificate.getSigAlgOID();
        for (String str : SIGNATURE_ALGORITHM_OID_BLACKLIST) {
            if (sigAlgOID.equals(str)) {
                throw new CertificateException(b.b("Signature uses an insecure hash function: ", sigAlgOID));
            }
        }
    }
}
