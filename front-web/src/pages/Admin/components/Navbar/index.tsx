import React from 'react';

import './styles.scss';

const Navbar = (): JSX.Element => (
  <nav className="admin-container-nav">
    <ul>
      <a href="admin" className="admin-nav-item active">
        <li>Meus Produtos</li>
      </a>
      <a href="admin" className="admin-nav-item">
        <li>Minhas Categorias</li>
      </a>
      <a href="admin" className="admin-nav-item">
        <li>Meus Usuários</li>
      </a>
    </ul>
  </nav>
);

export default Navbar;
