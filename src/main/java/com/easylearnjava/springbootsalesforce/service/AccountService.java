package com.easylearnjava.springbootsalesforce.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import com.easylearnjava.springbootsalesforce.model.Account;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Component
@Lazy
public class AccountService {

    private static final String REST_VERSION = "40.0";

    @Bean
    private OAuth2RestTemplate oAuth2RestTemplate(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext context) {
        return new OAuth2RestTemplate(resource, context);
    }

    @Autowired
    private OAuth2RestTemplate restTemplate;

    @SuppressWarnings("unchecked")
    private static String restUrl(OAuth2Authentication principal) {
        HashMap<String, Object> details = (HashMap<String, Object>) principal.getUserAuthentication().getDetails();
        HashMap<String, String> urls = (HashMap<String, String>) details.get("urls");
        return urls.get("rest").replace("{version}", REST_VERSION);
    }

    public List<Account> accounts(OAuth2Authentication principal) {
        String url = restUrl(principal) + "query/?q={q}";

        Map<String, String> params = new HashMap<>();
        params.put("q", "SELECT Id, Name, Type, Industry, BillingAddress FROM Account");
        List<Account> accLst = restTemplate.getForObject(url, QueryResultAccount.class, params).records; 
        
        return accLst;
    }    

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class QueryResult<T> {
        public List<T> records;
    }

    private static class QueryResultAccount extends QueryResult<Account> {}

}