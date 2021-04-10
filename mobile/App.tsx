import 'react-native-gesture-handler';
import React from 'react';
import { NavigationContainer } from '@react-navigation/native';

import Routes from './src/routes';

const App = (): JSX.Element => {
  return (
    <NavigationContainer>
      {/* Rotas */}
      <Routes />
    </NavigationContainer>
  );
}

export default App;