import React, { useEffect } from 'react';
import { Alert, Button, Text, View } from 'react-native';
import VoiceToText from './components/VoiceToText';

const App = () => {
  const [result, setResult] = React.useState('');
  const [partialResult, setPartialResult] = React.useState('');

  useEffect(() => {
    console.log("VoiceToText", VoiceToText)
    const subs = [
      VoiceToText.addListener('onSpeechResults', (results) => {
        setResult(results[0]);
      }),
      VoiceToText.addListener('onSpeechPartialResults', (results) => {
        setPartialResult(results[0]);
      }),
      VoiceToText.addListener('onSpeechError', (error) => {
        console.error('Speech recognition error:', error);
        Alert.alert('Speech recognition error', `Error code: ${error.error}. Please check your network connection and try again.`);
        
        // Log additional error details for debugging
        console.log('Error details:', JSON.stringify(error));

        // Check if the error is specifically network-related
        if (error.error === 'Error code: 9') {
          console.log('This error might not be network-related. Check service status or API limits.');
        }

        // Retry logic: Attempt to restart the speech recognition service
        setTimeout(() => {
          VoiceToText.startListening();
        }, 3000); // Retry after 3 seconds
      }),
    ];

    return () => subs.forEach(sub => sub());
  }, []);

  return (
    <View style={{ flex: 1, justifyContent: 'center', padding: 20 }}>
      <Button title="Start Listening" onPress={() => VoiceToText.startListening()} />
      <Text>Partial Result: {partialResult}</Text>
      <Text>Final Result: {result}</Text>
    </View>
  );
};

export default App;