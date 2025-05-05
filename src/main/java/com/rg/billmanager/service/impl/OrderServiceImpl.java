package com.rg.billmanager.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rg.billmanager.dto.order.DeleteOrderRequest;
import com.rg.billmanager.dto.order.OrderRequest;
import com.rg.billmanager.dto.order.cart.items.CartItem;
import com.rg.billmanager.dto.order.cart.items.CartItemsResponse;
import com.rg.billmanager.dto.order.cart.items.CartTotal;
import com.rg.billmanager.dto.template.DocumentResponse;
import com.rg.billmanager.enums.OutFormat;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.rg.billmanager.constants.UrlConstants.AUTH_INFO;
import static com.rg.billmanager.constants.UrlConstants.BILLMGR;
import static com.rg.billmanager.constants.UrlConstants.FUNC;
import static com.rg.billmanager.constants.UrlConstants.HTTPS;
import static com.rg.billmanager.constants.UrlConstants.OUT;
import static com.rg.billmanager.constants.UrlConstants.PRICELIST;
import static com.rg.billmanager.enums.FunctionName.CART;
import static com.rg.billmanager.enums.FunctionName.ORDER_PARAM;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final RestTemplate restTemplate;
    private final ErrorUtility errorUtility;
    private final ObjectMapper objectMapper;
    private final CartResponseMapper cartResponseMapper;

    @Override
    public void addItemToCart(OrderRequest orderRequest) throws JsonProcessingException {
        String url = UriComponentsBuilder.newInstance()
                .scheme(HTTPS)
                .host(orderRequest.getBaseUrl())
                .path(BILLMGR)
                .build().toString();

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
        String url = UriComponentsBuilder.newInstance()
                .scheme(HTTPS)
                .host(baseUrl)
                .path(BILLMGR)
                .queryParam(AUTH_INFO, authData)
                .queryParam(FUNC, CART.getName())
                .queryParam(OUT, OutFormat.JSON.getName())
                .build().toString();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        DocumentResponse documentResponse = objectMapper.readValue(response.getBody(), DocumentResponse.class);

        CartTotal cartTotal = cartResponseMapper.parseCartTotal(documentResponse);

        List<CartItem> cartItems = cartResponseMapper.parseCartItems(documentResponse);

        return new CartItemsResponse(cartItems, cartTotal);
    }

    @Override
    public void removeCartItem(DeleteOrderRequest deleteOrderRequest) throws JsonProcessingException {
        String url = UriComponentsBuilder.newInstance()
                .scheme(HTTPS)
                .host(deleteOrderRequest.getBaseUrl())
                .path(BILLMGR)
                .build().toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> params = new HashMap<>();
        params.put(AUTH_INFO, deleteOrderRequest.getAuthData());
        params.put("id", deleteOrderRequest.getCartItemId().toString());
        params.put(FUNC, CART.getName());
        params.put("promocode", "");
        params.put("sok", "ok");
        params.put("elid", "");
        params.put("clicked_button", "delete");
        params.put(OUT, OutFormat.XJSON.getName());

        HttpEntity<String> request = buildFormUrlEncodedEntity(params);
        String response = restTemplate.postForObject(url, request, String.class);

        String error = errorUtility.parseJsonErrorMessage(response);

        if (!error.isEmpty()) {
            throw new InvalidOrderException(error);
        }
    }

    private Map<String, String> getRequestParams(OrderRequest orderRequest) {
        Map<String, String> params = new HashMap<>();
        params.put(AUTH_INFO, orderRequest.getAuthData());
        params.put("order_period", orderRequest.getOrderPeriod());
        params.put("show_btn", "pay_order");
        params.put("autoprolong_unavailable", "");
        params.put("autoprolong", orderRequest.getAutoprolong());
        params.put("item_id", "");
        params.put("lineitem_id", "");
        params.put(PRICELIST, orderRequest.getExternalId().toString());
        params.put("domain", "");
        params.put("force_use_new_cart", "on");
        params.put("ostempl", orderRequest.getOstempl());
        params.put("recipe", orderRequest.getRecipe());
        orderRequest.getAddons().entrySet().forEach(entry -> params.put(entry.getKey(), entry.getValue()));
        params.put("order_count", orderRequest.getOrderCount().toString());
        params.put(FUNC, ORDER_PARAM.getName());
        params.put("sok", "ok");
        params.put("elid", "");
        params.put("clicked_button", "order");
        params.put(OUT, OutFormat.XJSON.getName());
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
