import React from 'react';
import { NavLink } from 'react-router-dom';

import './styles.scss';

const Navbar = (): JSX.Element => (
  <nav className="admin-container-nav">
    <ul>
      <NavLink to="/admin/products" className="admin-nav-item">
        <li>Meus Produtos</li>
      </NavLink>
      <NavLink to="/admin/categories" className="admin-nav-item">
        <li>Minhas Categorias</li>
      </NavLink>
      <NavLink to="/admin/users" className="admin-nav-item">
        <li>Meus Usuários</li>
      </NavLink>
    </ul>
  </nav>
);

export default Navbar;
