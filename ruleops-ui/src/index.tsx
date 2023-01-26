import React from 'react';
import ReactDOM from 'react-dom/client';
import '@patternfly/patternfly/patternfly.css'
import { MyPF } from './App';

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);
root.render(
  <React.StrictMode>
    <MyPF />
  </React.StrictMode>
);
