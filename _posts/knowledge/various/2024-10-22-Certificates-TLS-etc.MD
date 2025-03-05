---
title: Certificates, TLS, etc.
date: 2024-10-22 00:00:00 +0100
categories: [ security ]
tags: [ certificates, TLS, cryptography ] # TAG names should always be lowercase
---

Knowledge related with certificates, cryptography and similar topics.

1. Private Key
    - Should never ever leave machine on which it was generated
    - No transmitting private key in any form
2. Certificate
    - Issued by (CA)
    - Issued for (Subject)
    - Subject Public Key
    - `Signature == Encrypted Subject Public Key`
        - `Subject Public Key * CA Private Key => Encrypted Subject Public Key`
3. CA
    - For a given Subject
    - Creates Signature (`Subject Public Key * CA Private Key => Encrypted Subject Public Key`)
    - Client can decrypt Signature using CA Public Key
    - If `Decrypted Subject Public Key == Subject Public Key` it means that Subject is legit
4. Intermediate CA
    - Leak of Root CA Private Key would be disastrous
    - That's why instead of calling Root CA, we call some "subclass" of CA called Intermediate CA
5. CSR
    - Subject Public Key
    - Send Subject CSR to CA
    - CA generates Certificate based on CSR
6. Root CA
    - Signature of Root CA is Self-Signed
    - `Issued by == Root CA == Issued for`
7. Formats
    1. PEM Format
        - It's base64 encoded certificate details
        - You may save it as `.crt` file to make it recognizable by software
    2. PKCS#12 Format
        - Contains both
            - Private Key
            - Certificate
        - Private Key secured by password
8. Extensions
    1. `.crt`
        - Makes it easy for software to recognize this is certificate
        - E.g. PEM Format file
    2. `.key`
        - Usually used to mark this is PEM Format Private Key
    3. `.p12`
        - Used for PKCS#12 Format
