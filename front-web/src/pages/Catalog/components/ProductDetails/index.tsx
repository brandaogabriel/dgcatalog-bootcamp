import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';

import './styles.scss';

import { ReactComponent as ArrowIcon } from 'core/assets/images/arrow.svg';
import ProductPrice from 'core/components/ProductPrice';
import { makeRequest } from 'core/utils/request';
import { Product } from 'core/types/Product';
import ProductInfoLoader from '../Loaders/ProductInfoLoader';
import ProductDescriptionLoader from '../Loaders/ProductDescriptionLoader';

type ParamsType = {
  id: string;
};

const ProductDetails = (): JSX.Element => {
  const { id } = useParams<ParamsType>();
  const [product, setProduct] = useState<Product>();
  const [isLoading, setIsLoading] = useState<boolean>(false);

  useEffect(() => {
    setIsLoading(true);
    makeRequest({
      url: `/products/${id}`,
    })
      .then(response => setProduct(response.data))
      .finally(() => setIsLoading(false));
  }, [id]);

  return (
    <div className="product-details-container">
      <div className="card-base border-radius-20 product-details">
        <Link to="/products" className="product-details-go-back">
          <ArrowIcon className="icon-go-back" />
          <h1 className="text-go-back">VOLTAR</h1>
        </Link>
        <div className="row">
          <div className="col-6 pr-5">
            {isLoading ? (
              <ProductInfoLoader />
            ) : (
              <>
                <div className="product-details-card border-radius-20 text-center">
                  <img
                    src={product?.imgUrl}
                    alt={product?.name}
                    className="product-details-image"
                  />
                </div>
              </>
            )}
            <h1 className="product-details-name">{product?.name}</h1>
            {product?.price && <ProductPrice price={product.price} />}
          </div>
          <div className="col-6 product-details-card border-radius-20">
            {isLoading ? (
              <ProductDescriptionLoader />
            ) : (
              <>
                <h1 className="product-description-title">
                  Descrição do produto
                </h1>
                <p className="product-description-text">
                  {product?.description}
                </p>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductDetails;
