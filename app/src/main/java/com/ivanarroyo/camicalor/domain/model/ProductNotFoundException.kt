package com.ivanarroyo.camicalor.domain.model

class ProductNotFoundException(productId: String) :
    Exception("No existe un producto con id '$productId'")
