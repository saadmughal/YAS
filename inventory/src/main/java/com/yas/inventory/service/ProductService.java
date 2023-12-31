package com.yas.inventory.service;

import com.yas.inventory.config.ServiceUrlConfig;
import com.yas.inventory.exception.AccessDeniedException;
import com.yas.inventory.exception.NotFoundException;
import com.yas.inventory.model.enumeration.FilterExistInWHSelection;
import com.yas.inventory.utils.AuthenticationUtils;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.product.ProductQuantityPostVm;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public ProductService(WebClient webClient, ServiceUrlConfig serviceUrlConfig) {
        this.webClient = webClient;
        this.serviceUrlConfig = serviceUrlConfig;
    }

    public ProductInfoVm getProduct(Long id) {
        String jwt = AuthenticationUtils.extractJwt();

        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.product())
                .path("/backoffice/products/" + id)
                .build()
                .toUri();
        return webClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .retrieve()
                .bodyToFlux(ProductInfoVm.class)
                .single()
                .block();
    }

    public List<ProductInfoVm> filterProducts(String productName, String productSku,
                                              List<Long> productIds, FilterExistInWHSelection selection) {
        String jwt = AuthenticationUtils.extractJwt();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", productName);
        params.add("sku", productSku);
        params.add("selection", selection.name());
        if (!CollectionUtils.isEmpty(productIds)) {
            params.add("productIds", productIds.stream().map(Object::toString).collect(Collectors.joining(",")));
        }

        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.product())
                .path("/backoffice/products/for-warehouse")
                .queryParams(params)
                .build()
                .toUri();
        return webClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .retrieve()
                .bodyToFlux(ProductInfoVm.class)
                .collectList()
                .block();
    }

    public Void updateProductQuantity(List<ProductQuantityPostVm> productQuantityPostVms) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();

        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.product())
                .path("/backoffice/products/update-quantity")
                .buildAndExpand()
                .toUri();

        return webClient.put()
                .uri(url)
                .headers(h->h.setBearerAuth(jwt))
                .bodyValue(productQuantityPostVms)
                .retrieve()
                .onStatus(
                        HttpStatus.UNAUTHORIZED::equals,
                        response -> response.bodyToMono(String.class).map(AccessDeniedException::new))
                .onStatus(
                        HttpStatus.FORBIDDEN::equals,
                        response -> response.bodyToMono(String.class).map(AccessDeniedException::new))
                .onStatus(
                        HttpStatus.BAD_REQUEST::equals,
                        response -> response.bodyToMono(String.class).map(NotFoundException::new))
                .onStatus(
                        HttpStatus.NOT_FOUND::equals,
                        response -> response.bodyToMono(String.class).map(NotFoundException::new))
                .bodyToMono(void.class)
                .block();
    }
}
