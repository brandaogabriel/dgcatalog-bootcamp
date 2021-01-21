import React from 'react';

import { ReactComponent as ArrowIcon } from '../../assets/images/arrow.svg';
import { Link } from 'react-router-dom';

import './styles.scss';

type Props = {
  text: string;
};

const ButtonIcon = ({ text }: Props): JSX.Element => (
  <div className="d-flex">
    <button className="btn btn-primary btn-icon">
      <h5>{text}</h5>
    </button>
    <div className="btn-icon-content">
      <ArrowIcon />
    </div>
  </div>
);

export default ButtonIcon;