import axios, { AxiosPromise, Method } from 'axios';

type RequestParams = {
  method?: Method;
  url: string;
  data?: Record<string, unknown>;
  params?: Record<string, unknown>;
};

const BASE_URL = 'http://localhost:3000';

export const makeRequest = ({
  method = 'GET',
  url,
  data,
  params,
}: RequestParams): AxiosPromise<any> => {
  return axios({
    method,
    url: `${BASE_URL}${url}`,
    data,
    params,
  });
};
