package org.ecommerce.project.repositories;

import org.ecommerce.project.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findCartItemsByCart_CartIdAndProduct_ProductId(Long cartCartId, Long productProductId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.cartId= ?1 AND ci.product.productId=?2")
    void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);
}
