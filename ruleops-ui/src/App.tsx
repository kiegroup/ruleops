import {
  Breadcrumb,
  BreadcrumbItem, Masthead,
  MastheadBrand,
  MastheadContent,
  MastheadMain,
  MastheadToggle,
  Nav,
  NavItem,
  NavList,
  Page,
  PageSection,
  PageSectionVariants,
  PageSidebar,
  PageToggleButton,
  SkipToContent,
  Text,
  TextContent
} from '@patternfly/react-core';
import BarsIcon from '@patternfly/react-icons/dist/esm/icons/bars-icon';
import React from 'react';
import './app.css';
import { Dashboard } from './Dashboard';

interface NavOnSelectProps {
  groupId: number | string;
  itemId: number | string;
  to: string;
  event: React.FormEvent<HTMLInputElement>;
}

export const MyPF: React.FunctionComponent = () => {
  const [activeItem, setActiveItem] = React.useState(0);

  const onNavSelect = (selectedItem: NavOnSelectProps) => {
    typeof selectedItem.itemId === 'number' && setActiveItem(selectedItem.itemId);
  };

  const dashboardBreadcrumb = (
    <Breadcrumb>
      <BreadcrumbItem>Home</BreadcrumbItem>
      <BreadcrumbItem to="#">Dashboard</BreadcrumbItem>
      {/* <BreadcrumbItem to="#">Section title</BreadcrumbItem>
      <BreadcrumbItem to="#" isActive>
        Section landing
      </BreadcrumbItem> */}
    </Breadcrumb>
  );

  const masthead = (
    <Masthead>
      <MastheadToggle>
        <PageToggleButton variant="plain" aria-label="Global navigation">
          <BarsIcon />
        </PageToggleButton>
      </MastheadToggle>
      <MastheadMain>
        <MastheadBrand>
          <img src={"img/RuleOps.png"} alt="RuleOps Logo" />
        </MastheadBrand>
      </MastheadMain>
      <MastheadContent>
      </MastheadContent>
    </Masthead>
  );

  const pageNav = (
    <Nav onSelect={onNavSelect}>
      <NavList>
        <NavItem itemId={0} isActive={activeItem === 0} to="#">
          Dashboard
        </NavItem>
        <NavItem itemId={1} isActive={activeItem === 1} to="#about">
          About
        </NavItem>
      </NavList>
    </Nav>
  );

  const sidebar = <PageSidebar nav={pageNav} />;

  const mainContainerId = 'main-content';

  const pageSkipToContent = <SkipToContent href={`#${mainContainerId}`}>Skip to content</SkipToContent>;

  return (
    <Page
      header={masthead}
      sidebar={sidebar}
      isManagedSidebar
      skipToContent={pageSkipToContent}
      breadcrumb={dashboardBreadcrumb}
      mainContainerId={mainContainerId}
      isBreadcrumbWidthLimited
      isBreadcrumbGrouped
      additionalGroupedContent={
        <PageSection variant={PageSectionVariants.light} isWidthLimited>
          <TextContent>
            <Text component="h1">Main Dashboard</Text>
            <Text component="p">
              Using this dashboard you can overview the advices emitted by RuleOps,<br />
              using internally the Drools rule engine while inspecting your Kubernetes cluster.
            </Text>
          </TextContent>
        </PageSection>
      }
      groupProps={{
        stickyOnBreakpoint: { default: 'top' }
      }}
    >
      <Dashboard/>
    </Page>
  );
};
