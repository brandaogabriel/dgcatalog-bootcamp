import React from 'react';
import { Route, Switch } from 'react-router-dom';
import Form from './Form';
import List from './List';

const Products = (): JSX.Element => {
  return (
    <div>
      <Switch>
        <Route path="/admin/products" exact>
          <List />
        </Route>
        <Route path="/admin/products/create">
          <Form />
        </Route>
        <Route path="/admin/products/:id">
          <h1>Edição de produtos</h1>
        </Route>
      </Switch>
    </div>
  );
};

export default Products;
