package com.rg.billmanager.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rg.billmanager.dto.order.OrderRequest;
import com.rg.billmanager.dto.order.cart.items.CartItem;
import com.rg.billmanager.dto.order.cart.items.CartItemsResponse;
import com.rg.billmanager.dto.order.cart.items.CartTotal;
import com.rg.billmanager.dto.template.DocumentResponse;
import com.rg.billmanager.exception_handler.exception.InvalidOrderException;
import com.rg.billmanager.mapper.CartResponseMapper;
import com.rg.billmanager.service.OrderService;
import com.rg.billmanager.utility.ErrorUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final RestTemplate restTemplate;
    private final ErrorUtility errorUtility;
    private final ObjectMapper objectMapper;
    private final CartResponseMapper cartResponseMapper;

    @Override
    public void createOrder(OrderRequest orderRequest) throws JsonProcessingException {
        String url = String.format("https://%s/billmgr", orderRequest.getBaseUrl());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> params = getRequestParams(orderRequest);
        HttpEntity<String> request = buildFormUrlEncodedEntity(params);
        String response = restTemplate.postForObject(url, request, String.class);

        String error = errorUtility.parseJsonErrorMessage(response);

        if (!error.isEmpty()) {
            throw new InvalidOrderException(error);
        }
    }

    @Override
    public CartItemsResponse getCartItems(String baseUrl, String authData) throws JsonProcessingException {
        String url = String.format("https://%s/billmgr?authinfo=%s&func=cart&out=json", baseUrl, authData);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        DocumentResponse documentResponse = objectMapper.readValue(response.getBody(), DocumentResponse.class);

        CartTotal cartTotal = cartResponseMapper.parseCartTotal(documentResponse);

        List<CartItem> cartItems = cartResponseMapper.parseCartItems(documentResponse);

        return new CartItemsResponse(cartItems, cartTotal);
    }

    private Map<String, String> getRequestParams(OrderRequest orderRequest) {
        Map<String, String> params = new HashMap<>();
        params.put("authinfo", orderRequest.getAuthData());
        params.put("order_period", orderRequest.getOrderPeriod());
        params.put("show_btn", "pay_order");
        params.put("autoprolong_unavailable", "");
        params.put("autoprolong", orderRequest.getAutoprolong());
        params.put("item_id", "");
        params.put("lineitem_id", "");
        params.put("pricelist", orderRequest.getExternalId().toString());
        params.put("domain", "");
        params.put("force_use_new_cart", "on");
        params.put("ostempl", orderRequest.getOstempl());
        params.put("recipe", orderRequest.getRecipe());
        orderRequest.getAddons().entrySet().forEach(entry -> params.put(entry.getKey(), entry.getValue()));
        params.put("order_count", orderRequest.getOrderCount().toString());
        params.put("func", "v2.vds.order.param");
        params.put("sok", "ok");
        params.put("elid", "");
        params.put("clicked_button", "order");
        params.put("out", "xjson");
        return params;
    }

    private HttpEntity<String> buildFormUrlEncodedEntity(Map<String, String> params) {
        String body = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return new HttpEntity<>(body, headers);
    }
}
