import * as React from 'react';
import { ServicesIcon } from '@patternfly/react-icons';
import {
  PageSection,
  Title,
  Button,
  EmptyState,
  EmptyStateVariant,
  EmptyStateIcon,
  EmptyStateBody,
  EmptyStateSecondaryActions,
  Text,
  TextContent,
  TextVariants,
} from '@patternfly/react-core';

export interface ISupportProps {
  sampleProp?: string;
}

// eslint-disable-next-line prefer-const
let Support: React.FunctionComponent<ISupportProps> = () => (
  <PageSection>
    <EmptyState variant={EmptyStateVariant.full}>
      <EmptyStateIcon icon={ServicesIcon} />
      <Title headingLevel="h1" size="lg">
        Drools RuleOps
      </Title>
      <EmptyStateBody>
        <TextContent>
          <Text component="p">
            Working PoC demo.
          </Text>
          <Text component={TextVariants.small}>
            Please refer to the documentation for additional information.
          </Text>
        </TextContent>
      </EmptyStateBody>
      <Button variant="primary">Primary Action</Button>
      <EmptyStateSecondaryActions>
        <Button variant="link">Multiple</Button>
        <Button variant="link">Action Buttons</Button>
        <Button variant="link">Can</Button>
        <Button variant="link">Go here</Button>
        <Button variant="link">In the secondary</Button>
        <Button variant="link">Action area</Button>
      </EmptyStateSecondaryActions>
    </EmptyState>
  </PageSection>
);

export { Support };
