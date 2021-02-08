import { makeRequest } from 'core/utils/request';
import React, { useState } from 'react';
import BaseForm from '../../BaseForm';

import './styles.scss';

type FormState = {
  name: string;
  price: string;
  category: string;
  description: string;
};

type FormEvent = React.ChangeEvent<
  HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
>;

const Form = (): JSX.Element => {
  const [formData, setFormData] = useState<FormState>({
    name: '',
    price: '',
    category: '1',
    description: '',
  });

  const handleOnChange = (event: FormEvent): void => {
    const name = event.target.name;
    const value = event.target.value;

    setFormData(data => ({ ...data, [name]: value }));
  };

  const handleSubmit = (event: React.ChangeEvent<HTMLFormElement>): void => {
    event.preventDefault();

    const payload = {
      ...formData,
      imgUrl:
        'https://compass-ssl.xbox.com/assets/83/53/83534a33-0998-43dc-915a-4ec0a686d679.jpg?n=10202018_Panes-3-up-1400_Hero-SX_570x570.jpg',
      categories: [
        {
          id: formData.category,
        },
      ],
    };

    makeRequest({ method: 'POST', url: '/products', data: payload }).then(
      () => {
        setFormData({ name: '', price: '', category: '', description: '' });
      },
    );

    console.log(payload);
  };

  return (
    <form onSubmit={handleSubmit}>
      <BaseForm title={'CADASTRAR UM PRODUTO'}>
        <div className="row">
          <div className="col-6">
            <input
              name="name"
              value={formData.name}
              type="text"
              className="form-control mb-5"
              placeholder="Nome do produto"
              onChange={handleOnChange}
            />
            <select
              name="category"
              value={formData.category}
              className="form-control mb-5"
              onChange={handleOnChange}
            >
              <option value="1">Livros</option>
              <option value="3">Computadores</option>
              <option value="2">Eletrônicos</option>
            </select>
            <input
              name="price"
              value={formData.price}
              type="text"
              className="form-control"
              placeholder="Preço"
              onChange={handleOnChange}
            />
          </div>
          <div className="col-6">
            <textarea
              onChange={handleOnChange}
              name="description"
              className="form-control"
              cols={30}
              rows={10}
            />
          </div>
        </div>
      </BaseForm>
    </form>
  );
};

export default Form;
