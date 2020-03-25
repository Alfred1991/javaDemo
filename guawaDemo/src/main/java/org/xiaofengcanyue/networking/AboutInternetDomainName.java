package org.xiaofengcanyue.networking;

import com.google.common.net.InternetDomainName;

/**
 * Top-Level Domain(TLD): A single-label domain with no children,such as com or au.
 * Registry suffix: A domain,such as com or co.uk,controlled by a domain name registry (e.g. Verisign for com),
 *   under which people can register subdomains through a domain name registrar (e.g. Namecheap).
 *   Such domain name registrations are legally protected by internet governing bodies like ICANN.
 * Public suffix: This category is a superset of the registry suffixes, additionally including suffixes like blogspot.com that are not registry-controlled but allow the public to register subdomains.
 *   There are several common cases where it is more appropriate to categorize domains by public suffix rather than registry suffix.
 *   For example, one should never set cookies on a public suffix.
 * Effective Top-Level Domain: A deprecated synonym for "public suffix".
 */
public class AboutInternetDomainName {
    public static void main(String[] args) {
        aboutInternetDomainName("www.blogspot.com");
    }

    public static void aboutInternetDomainName(String str){
        System.out.println("public suffix:"+InternetDomainName.from(str).publicSuffix());
        System.out.println("registry suffix:"+InternetDomainName.from(str).registrySuffix());
        System.out.println("top domain under registry suffix:"+InternetDomainName.from(str).topDomainUnderRegistrySuffix());
        System.out.println("top private domain:"+InternetDomainName.from(str).topPrivateDomain());
        System.out.println("parent:"+InternetDomainName.from(str).parent());
    }

}
