package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WishlistDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WishlistDTO.class);
        WishlistDTO wishlistDTO1 = new WishlistDTO();
        wishlistDTO1.setId(1L);
        WishlistDTO wishlistDTO2 = new WishlistDTO();
        assertThat(wishlistDTO1).isNotEqualTo(wishlistDTO2);
        wishlistDTO2.setId(wishlistDTO1.getId());
        assertThat(wishlistDTO1).isEqualTo(wishlistDTO2);
        wishlistDTO2.setId(2L);
        assertThat(wishlistDTO1).isNotEqualTo(wishlistDTO2);
        wishlistDTO1.setId(null);
        assertThat(wishlistDTO1).isNotEqualTo(wishlistDTO2);
    }
}
