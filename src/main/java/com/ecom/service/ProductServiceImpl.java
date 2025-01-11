package com.ecom.service;

import com.ecom.exceptions.APIException;
import com.ecom.exceptions.ResourceNotFoundException;
import com.ecom.model.Cart;
import com.ecom.model.CartItem;
import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.payload.CartDTO;
import com.ecom.payload.ProductDTO;
import com.ecom.payload.ProductResponse;
import com.ecom.repository.CartItemRepository;
import com.ecom.repository.CartRepository;
import com.ecom.repository.CategoryRepository;
import com.ecom.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final CartItemRepository cartItemRepository;

    private final ModelMapper modelMapper;

    private final FileService fileService;

    @Value("${project.image}")
    private String imagesPath;

    @Override
    @Transactional
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Product product = modelMapper.map(productDTO, Product.class);


        if (productRepository.existsByProductNameAndCategory(product.getProductName(), category)) {
            throw new APIException("Product already exists");
        }

        product.setImage("default.png ");

        product.setCategory(category);

        double specialPrice = product.getPrice() - ((product.getPrice() * (product.getDiscount() / 100)));
        product.setSpecialPrice(specialPrice);

        Product savedProduct = productRepository.save(product);

        return modelMapper.map(savedProduct, ProductDTO.class);

    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageConfig = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Product> productsPage = productRepository.findAll(pageConfig);
        List<Product> products = productsPage.getContent();

        if(products.isEmpty()){
            throw new APIException("No products found");
        }

        List<ProductDTO> productDTOS = products.stream()
                .map((product) -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = ProductResponse.builder()
                .content(productDTOS)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(productsPage.getTotalPages())
                .totalElements(productsPage.getTotalElements())
                .lastPage(productsPage.isLast())
                .build();

        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                            ? Sort.by(sortBy).ascending()
                            : Sort.by(sortBy).descending();

        Pageable pageConfig = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productsPage = productRepository.findByCategory(category, pageConfig);
        List<Product> products = productsPage.getContent();

        if(products.isEmpty()){
            throw new APIException("No products found");
        }

        List<ProductDTO> productDTOS = products.stream()
                .map((product) -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = ProductResponse.builder()
                .content(productDTOS)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(productsPage.getTotalPages())
                .totalElements(productsPage.getTotalElements())
                .lastPage(productsPage.isLast())
                .build();

        return productResponse;


    }

    @Override
    public ProductResponse searchByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageConfig = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productsPage = productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%", pageConfig);
        List<Product> products = productsPage.getContent();


        if(products.isEmpty()){
            throw new APIException("No products found");
        }

        List<ProductDTO> productDTOS = products.stream()
                .map((product) -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = ProductResponse.builder()
                .content(productDTOS)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(productsPage.getTotalPages())
                .totalElements(productsPage.getTotalElements())
                .lastPage(productsPage.isLast())
                .build();

        return productResponse;


    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {

        Product productFromDB = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        Product product = modelMapper.map(productDTO, Product.class);

        double oldPrice = productFromDB.getSpecialPrice();
        double newPrice = product.getPrice() *  (1 - (product.getDiscount() / 100));

        if(newPrice != oldPrice || productFromDB.getQuantity() != product.getQuantity()){
            List<CartItem> cartItems = cartItemRepository.findCartItemByProduct(productFromDB);
            cartItems.forEach((cartItem) -> {
                if(product.getQuantity() < cartItem.getQuantity()){
                    Cart cart = cartItem.getCart();
                    cart.setTotalPrice(cart.getTotalPrice() - (oldPrice * cartItem.getQuantity()));
                    cartItemRepository.delete(cartItem);
                    return;
                }
                Cart cart = cartItem.getCart();
                cart.setTotalPrice(cart.getTotalPrice() + ((newPrice - oldPrice) * cartItem.getQuantity()));
            });
        }

        productFromDB.setProductName(product.getProductName());
        productFromDB.setProductDescription(product.getProductDescription());
        productFromDB.setQuantity(product.getQuantity());
        productFromDB.setDiscount(product.getDiscount());
        productFromDB.setPrice(product.getPrice());
        productFromDB.setSpecialPrice(newPrice);


        Product savedProduct = productRepository.save(productFromDB);
        return modelMapper.map(savedProduct, ProductDTO.class);

    }

    @Override
    @Transactional
    public ProductDTO deleteProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        List<CartItem> cartItems = cartItemRepository.findCartItemByProduct(product);

        cartItems.forEach((cartItem) -> {
            Cart cart = cartItem.getCart();
            cart.setTotalPrice(cart.getTotalPrice() - (product.getSpecialPrice() * cartItem.getQuantity()));
        });

        productRepository.delete(product);

        return modelMapper.map(product, ProductDTO.class);

    }

    @Override
    @Transactional
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        String fileName = fileService.uploadImage(imagesPath, image);

        product.setImage(fileName);

        Product savedProduct = productRepository.save(product);

        return modelMapper.map(savedProduct, ProductDTO.class);

    }

}
