package com.rg.billmanager.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rg.billmanager.contracts.requests.CreateOrderRequest;
import com.rg.billmanager.contracts.requests.DeleteOrderRequest;
import com.rg.billmanager.contracts.requests.ExtendOrderRequest;
import com.rg.billmanager.contracts.requests.OrderStatusRequest;
import com.rg.billmanager.contracts.responses.OrderStatusResponse;
import com.rg.billmanager.dto.order.cart.items.CartItem;
import com.rg.billmanager.dto.order.cart.items.CartItemsResponse;
import com.rg.billmanager.dto.order.cart.items.CartTotal;
import com.rg.billmanager.dto.DocumentResponse;
import com.rg.billmanager.enums.OutFormat;
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.exception_handler.exception.InvalidOrderException;
import com.rg.billmanager.mapper.CartResponseMapper;
import com.rg.billmanager.mapper.OrderResponseMapper;
import com.rg.billmanager.service.OrderService;
import com.rg.billmanager.utility.ErrorUtility;
import com.rg.billmanager.utility.UrlUtils;
import com.rg.billmanager.utility.requestbuilder.ParamBuilderRegistry;
import com.rg.billmanager.utility.requestbuilder.UrlBuilderRegistry;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestParamBuilder;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestUrlBuilder;
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
import static com.rg.billmanager.constants.UrlConstants.BILLMGR;
import static com.rg.billmanager.constants.UrlConstants.FUNC;
import static com.rg.billmanager.constants.UrlConstants.HTTPS;
import static com.rg.billmanager.constants.UrlConstants.OUT;
import static com.rg.billmanager.enums.FunctionName.CART;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final RestTemplate restTemplate;
    private final ErrorUtility errorUtility;
    private final ObjectMapper objectMapper;
    private final CartResponseMapper cartResponseMapper;
    private final OrderResponseMapper orderResponseMapper;
    private final ParamBuilderRegistry paramBuilderRegistry;
    private final UrlBuilderRegistry urlBuilderRegistry;

    @Override
    public String createOrder(CreateOrderRequest createOrderRequest) throws JsonProcessingException {
        RequestUrlBuilder<CreateOrderRequest> urlBuilder = urlBuilderRegistry.getUrlBuilder(RequestType.CREATE_ORDER);
        RequestParamBuilder<CreateOrderRequest> paramBuilder = paramBuilderRegistry
                .getParamBuilder(RequestType.CREATE_ORDER);

        String url = urlBuilder.buildUrl(createOrderRequest);

        Map<String, String> params = paramBuilder.buildParams(createOrderRequest);

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
        RequestUrlBuilder<OrderStatusRequest> urlBuilder = urlBuilderRegistry.getUrlBuilder(RequestType.ORDER_STATUS);
        OrderStatusRequest orderStatusRequest = new OrderStatusRequest(baseUrl, authData, remoteIds);
        String url = urlBuilder.buildUrl(orderStatusRequest);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        DocumentResponse documentResponse = objectMapper.readValue(response.getBody(), DocumentResponse.class);

        return orderResponseMapper.parseOrderStatuses(documentResponse, orderStatusRequest.getRemoteIds());
    }

    @Override
    public void extendService(ExtendOrderRequest extendOrderRequest) throws JsonProcessingException {
        RequestUrlBuilder<ExtendOrderRequest> urlBuilder = urlBuilderRegistry.getUrlBuilder(RequestType.EXTEND_ORDER);
        RequestParamBuilder<ExtendOrderRequest> paramBuilder = paramBuilderRegistry
                .getParamBuilder(RequestType.EXTEND_ORDER);

        String url = urlBuilder.buildUrl(extendOrderRequest);
        Map<String, String> params = paramBuilder.buildParams(extendOrderRequest);
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

    private String getItemId(String json) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(json);
        JsonNode lineItemIdNode = root.path("doc").path("item.id").path("$");

        return !lineItemIdNode.isMissingNode() ? lineItemIdNode.asText() : "";
    }
}
