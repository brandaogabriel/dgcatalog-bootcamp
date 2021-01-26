import React from 'react';
import { Link } from 'react-router-dom';

import { ReactComponent as HomeImage } from 'core/assets/images/home-image.svg';
import ButtonIcon from 'core/components/ButtonIcon';

import './styles.scss';

const Home = (): JSX.Element => (
  <div className="home-container">
    <div className="row home-content card-base border-radius-20">
      <div className="col-6">
        <h1 className="home-text-title">
          Conheça o melhor <br />
          catálogo de produtos
        </h1>
        <p className="home-text-subtitle">
          Ajudaremos você a encontrar os melhores <br />
          produtos disponíveis no mercado.
        </p>
        <Link to="/products">
          <ButtonIcon text={'inicie agora a sua busca'} />
        </Link>
      </div>
      <div className="col-6">
        <HomeImage className="home-image" />
      </div>
    </div>
  </div>
);

export default Home;
