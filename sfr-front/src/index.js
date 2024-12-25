import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import { ApolloProvider } from "@apollo/react-hooks";
import client from './apolloSetup';
import 'bootstrap/dist/css/bootstrap.css';
import './index.css';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <ApolloProvider client={client}>
      <div>
        <small>You are running this application in <b>{process.env.NODE_ENV}</b> mode.</small><br/>
        <small>Back end is: <b>{process.env.REACT_APP_SFR_BACK_HOST}</b></small>
      </div>
      <App />
      <div id="node-info-popup" className="tooltip alert alert-transparent"></div>
    </ApolloProvider>
    
  </React.StrictMode>
);