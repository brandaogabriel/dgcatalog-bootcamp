import React from 'react';
import { Link, Route, Switch } from 'react-router-dom';

const Products = (): JSX.Element => {
  return (
    <div>
      <Link to="/admin/products" className="mr-5">
        Listar produtos
      </Link>
      <Link to="/admin/products/create" className="mr-5">
        Criar produtos
      </Link>
      <Link to="/admin/products/10" className="mr-5">
        Editar produtos
      </Link>
      <Switch>
        <Route path="/admin/products" exact>
          <h1>Listagem de produtos</h1>
        </Route>
        <Route path="/admin/products/create">
          <h1>Criação de produtos</h1>
        </Route>
        <Route path="/admin/products/:id">
          <h1>Edição de produtos</h1>
        </Route>
      </Switch>
    </div>
  );
};

export default Products;
