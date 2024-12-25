import { split, HttpLink } from '@apollo/client';
import { ApolloClient, InMemoryCache } from '@apollo/client';
import { getMainDefinition } from '@apollo/client/utilities';
import { GraphQLWsLink } from '@apollo/client/link/subscriptions';
import { createClient } from 'graphql-ws';

const env = process.env.REACT_APP_SFR_BACK_HOST || "localhost"
const httpLink = new HttpLink({
  uri: 'http://'+env+':8080/graphql'
});

const wsLink = new GraphQLWsLink(createClient({
  url: 'ws://'+env+':8080/subscriptions',
}));

const splitLink = split(
  ({ query }) => {
    const definition = getMainDefinition(query);
    return (
      definition.kind === 'OperationDefinition' &&
      definition.operation === 'subscription'
    );
  },
  wsLink,
  httpLink,
);

const defaultOptions: DefaultOptions = {
  watchQuery: {
    fetchPolicy: 'no-cache',
    errorPolicy: 'ignore',
  },
  query: {
    fetchPolicy: 'no-cache',
    errorPolicy: 'all',
  },
};

export default new ApolloClient({
  link: splitLink,
  cache: new InMemoryCache(),
  defaultOptions: defaultOptions,
});