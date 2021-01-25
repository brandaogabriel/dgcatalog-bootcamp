import React from 'react';

import './styles.scss';

import { ReactComponent as ProductImage } from '../../../../core/assets/images/product.svg';
import ProductPrice from '../../../../core/components/ProductPrice';

const ProductCard = (): JSX.Element => (
  <div className="card-base border-radius-10 product-card">
    <ProductImage />
    <div className="product-info">
      <h6 className="product-name">Computador Desktop - Intel Core i7</h6>
    </div>
    <ProductPrice price="2.779,00" />
  </div>
);

export default ProductCard;
