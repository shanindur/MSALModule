/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React from 'react';
import {
  SafeAreaView,
  Text,
  TouchableOpacity,
  useColorScheme,
  NativeModules,
} from 'react-native';
import {Colors} from 'react-native/Libraries/NewAppScreen';

const {MSALModule} = NativeModules;

const App = () => {
  React.useEffect(() => {
    MSALModule.initializeMSAL();
  }, []);
  const isDarkMode = useColorScheme() === 'dark';

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
    justifyContent: 'center',
    alignItems: 'center',
    flex: 1,
  };
  const onSilentSign = () => {
    MSALModule.signInUser();
  };
  return (
    <SafeAreaView style={backgroundStyle}>
      <TouchableOpacity onPress={onSilentSign}>
        <Text>SIGN WITH MICROSOFT</Text>
      </TouchableOpacity>
    </SafeAreaView>
  );
};

export default App;
