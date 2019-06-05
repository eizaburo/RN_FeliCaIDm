/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {Component} from 'react';
//必要なComponentを追加
import {Platform, StyleSheet, Text, View, Button, NativeModules, NativeEventEmitter} from 'react-native';

//module取得
const { FeliCa } = NativeModules;
//event取得
const felicaEvents = new NativeEventEmitter(FeliCa);

const instructions = Platform.select({
  ios: 'Press Cmd+R to reload,\n' + 'Cmd+D or shake for dev menu',
  android:
    'Double tap R on your keyboard to reload,\n' +
    'Shake or press menu button for dev menu',
});

type Props = {};
export default class App extends Component<Props> {

  //stateにidm定義
  state = {
    idm: "xxxxxxxx"
  }

  componentWillMount(){
    //event追加
    felicaEvents.addListener("onTagDiscovered",({idm})=>{
      this.setState({idm:idm});
    });
  }

  render() {
    return (
      <View style={{flex:1,justifyContent:'center',alignItems:'center'}}>
        <Text>IDm:{this.state.idm}</Text>
        <View style={{marginTop:20}}></View>
        <Button
          title="start polling"
          onPress={()=>this.startPolling()}
        />
        <View style={{marginTop:20}}></View>
        <Button
          title="stop polling"
          onPress={()=>this.stopPolling()}
        />
      </View>
    );
  }

  //polling開始（callback定義するとかならず処理が必要みたい）
  startPolling = () => {
    this.setState({idm:"Reading..."});
    FeliCa.startPolling(x => console.log(x));
  }

  //polling停止（callback定義するとかならず処理が必要みたい）
  stopPolling = () => {
    FeliCa.stopPolling(x => console.log(x));
  }
}
