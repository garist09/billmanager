package com.rg.billmanager.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rg.billmanager.dto.order.DeleteOrderRequest;
import com.rg.billmanager.dto.order.ExtendOrderRequest;
import com.rg.billmanager.dto.order.OrderRequest;
import com.rg.billmanager.dto.order.OrderStatusResponse;
import com.rg.billmanager.dto.order.cart.items.CartItem;
import com.rg.billmanager.dto.order.cart.items.CartItemsResponse;
import com.rg.billmanager.dto.order.cart.items.CartTotal;
import com.rg.billmanager.dto.template.DocumentResponse;
import com.rg.billmanager.enums.OutFormat;
import com.rg.billmanager.exception_handler.exception.InvalidOrderException;
import com.rg.billmanager.mapper.CartResponseMapper;
import com.rg.billmanager.mapper.OrderResponseMapper;
import com.rg.billmanager.service.OrderService;
import com.rg.billmanager.utility.ErrorUtility;
import com.rg.billmanager.utility.UrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rg.billmanager.constants.UrlConstants.AUTH_INFO;
import static com.rg.billmanager.constants.UrlConstants.AUTOPROLONG;
import static com.rg.billmanager.constants.UrlConstants.BILLMGR;
import static com.rg.billmanager.constants.UrlConstants.DATACENTER;
import static com.rg.billmanager.constants.UrlConstants.ELID;
import static com.rg.billmanager.constants.UrlConstants.FUNC;
import static com.rg.billmanager.constants.UrlConstants.HTTPS;
import static com.rg.billmanager.constants.UrlConstants.ORDER_COUNT;
import static com.rg.billmanager.constants.UrlConstants.ORDER_PERIOD;
import static com.rg.billmanager.constants.UrlConstants.OSTEMPL;
import static com.rg.billmanager.constants.UrlConstants.OUT;
import static com.rg.billmanager.constants.UrlConstants.PERIOD;
import static com.rg.billmanager.constants.UrlConstants.PRICELIST;
import static com.rg.billmanager.constants.UrlConstants.RECIPE;
import static com.rg.billmanager.constants.UrlConstants.REMOTE_ID;
import static com.rg.billmanager.constants.UrlConstants.SKIP_BASKET;
import static com.rg.billmanager.constants.UrlConstants.SOK;
import static com.rg.billmanager.enums.FunctionName.CART;
import static com.rg.billmanager.enums.FunctionName.ORDER_PARAM;
import static com.rg.billmanager.enums.FunctionName.SERVICE_PROLONG;
import static com.rg.billmanager.enums.FunctionName.VDS;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final RestTemplate restTemplate;
    private final ErrorUtility errorUtility;
    private final ObjectMapper objectMapper;
    private final CartResponseMapper cartResponseMapper;
    private final OrderResponseMapper orderResponseMapper;

    @Override
    public String createOrder(OrderRequest orderRequest) throws JsonProcessingException {
        String url = UriComponentsBuilder.newInstance()
                .scheme(HTTPS)
                .host(orderRequest.getBaseUrl())
                .path(BILLMGR)
                .build().toString();

        Map<String, String> params = getRequestParams(orderRequest);
        HttpEntity<String> request = UrlUtils.buildFormUrlEncodedEntity(params);
        String response = restTemplate.postForObject(url, request, String.class);

        String error = errorUtility.parseJsonErrorMessage(response);

        if (!error.isEmpty()) {
            throw new InvalidOrderException(error);
        }

        return getItemId(response);
    }

    @Override
    public List<OrderStatusResponse> getOrderStatus(String baseUrl, String authData, List<String> remoteIds)
            throws JsonProcessingException {
        String url = UriComponentsBuilder.newInstance()
                .scheme(HTTPS)
                .host(baseUrl)
                .path(BILLMGR)
                .queryParam(AUTH_INFO, authData)
                .queryParam(FUNC, VDS.getName())
                .queryParam(OUT, OutFormat.XJSON.getName())
                .build().toString();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        DocumentResponse documentResponse = objectMapper.readValue(response.getBody(), DocumentResponse.class);

        return orderResponseMapper.parseOrderStatuses(documentResponse, remoteIds);
    }

    @Override
    public void extendService(ExtendOrderRequest extendOrderRequest) throws JsonProcessingException {
        String url = UriComponentsBuilder.newInstance()
                .scheme(HTTPS)
                .host(extendOrderRequest.getBaseUrl())
                .path(BILLMGR)
                .build().toString();

        Map<String, String> params = getProlongServiceParams(extendOrderRequest);
        HttpEntity<String> request = UrlUtils.buildFormUrlEncodedEntity(params);
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

        Map<String, String> params = new HashMap<>();
        params.put(AUTH_INFO, deleteOrderRequest.getAuthData());
        params.put("id", deleteOrderRequest.getCartItemId().toString());
        params.put(FUNC, CART.getName());
        params.put("promocode", "");
        params.put("sok", "ok");
        params.put("elid", "");
        params.put("clicked_button", "delete");
        params.put(OUT, OutFormat.XJSON.getName());

        HttpEntity<String> request = UrlUtils.buildFormUrlEncodedEntity(params);
        String response = restTemplate.postForObject(url, request, String.class);

        String error = errorUtility.parseJsonErrorMessage(response);

        if (!error.isEmpty()) {
            throw new InvalidOrderException(error);
        }
    }

    private Map<String, String> getRequestParams(OrderRequest orderRequest) {
        Map<String, String> params = new HashMap<>();
        params.put(AUTH_INFO, orderRequest.getAuthData());
        params.put(ORDER_PERIOD, orderRequest.getOrderPeriod());
        params.put(AUTOPROLONG, "off");
        if (orderRequest.getExternalId() != null) {
            params.put(PRICELIST, orderRequest.getExternalId().toString());
        }
        if (orderRequest.getDatacenterId() != null) {
            params.put(DATACENTER, orderRequest.getDatacenterId().toString());
        }
        params.put(OSTEMPL, orderRequest.getOstempl());
        params.put(RECIPE, orderRequest.getRecipe());
        if (orderRequest.getOrderCount() != null) {
            params.put(ORDER_COUNT, orderRequest.getOrderCount().toString());
        }
        params.put(FUNC, ORDER_PARAM.getName());
        params.put(SOK, "ok");
        params.put(SKIP_BASKET, "on");
        if (orderRequest.getRemoteId() != null) {
            params.put(REMOTE_ID, orderRequest.getRemoteId().toString());
        }
        params.put(OUT, OutFormat.XJSON.getName());
        return params;
    }

    private Map<String, String> getProlongServiceParams(ExtendOrderRequest extendOrderRequest) {
        Map<String, String> params = new HashMap<>();
        params.put(AUTH_INFO, extendOrderRequest.getAuthData());
        params.put(ELID, extendOrderRequest.getOrderId());
        params.put(PERIOD, extendOrderRequest.getPeriod());
        params.put(FUNC, SERVICE_PROLONG.getName());
        params.put(SOK, "ok");
        params.put(SKIP_BASKET, "on");
        params.put(OUT, OutFormat.XJSON.getName());
        return params;
    }

    private String getItemId(String json) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(json);
        JsonNode lineItemIdNode = root.path("doc").path("item.id").path("$");

        return !lineItemIdNode.isMissingNode() ? lineItemIdNode.asText() : "";
    }
}
