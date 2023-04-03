import {
  Button,
  ButtonVariant,
  DataList,
  DataListAction,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow, Drawer,
  DrawerActions,
  DrawerCloseButton,
  DrawerContent,
  DrawerContentBody,
  DrawerHead,
  DrawerPanelBody,
  DrawerPanelContent,
  Flex,
  FlexItem, PageSection, Stack,
  StackItem, Title, Toolbar, ToolbarContent, ToolbarGroup, ToolbarItem, ToolbarToggleGroup,
  Skeleton
} from '@patternfly/react-core';
import CodeIcon from '@patternfly/react-icons/dist/esm/icons/code-icon';
import ExclamationTriangleIcon from '@patternfly/react-icons/dist/esm/icons/exclamation-triangle-icon';
import FilterIcon from '@patternfly/react-icons/dist/esm/icons/filter-icon';
import SyncAltIcon from '@patternfly/react-icons/dist/esm/icons/sync-alt-icon';
import * as React from 'react';

const Dashboard: React.FunctionComponent = () => (
  <PageSection padding={{default: "noPadding"}}>
    <PrimaryDetailContentPadding />
  </PageSection>
)

export { Dashboard };

interface Advice {
  title: string,
  description: string,
};
const emptyAdvices: Advice[] = [];

const PrimaryDetailContentPadding : React.FunctionComponent = () => {
  const [isExpanded, setIsExpanded] = React.useState(false);
  const [selectedDataListItemId, setSelectedDataListItemId] = React.useState('');
  const [advices, setAdvices] = React.useState(emptyAdvices);
  const [foundAdvices, setFoundAdvices] = React.useState(false);
  const drawerRef = React.useRef<HTMLDivElement>();

  React.useEffect(() => {
    fetch("/advices")
      .then(res => res.json())
      .then(
        (result) => {
          setAdvices(result);
          setFoundAdvices(true);
        },
        (error) => {
          console.error(error);
        }
      )
  }, [])

  const onExpand = () => {
    drawerRef.current && drawerRef.current.focus();
  };

  const onClick = () => {
    setIsExpanded(!isExpanded);
  };

  const onCloseClick = () => {
    setIsExpanded(false);
  };

  const onSelectDataListItem = (id: any) => {
    setSelectedDataListItemId(id);
    onClick();
  };

  // const handleInputChange = (id: string, _event: React.FormEvent<HTMLInputElement>) => {
  //   setSelectedDataListItemId(id);
  // };

    const toggleGroupItems = (
      <React.Fragment>
        <ToolbarGroup variant="filter-group">
          <ToolbarItem>
            <Button variant="primary" icon={<SyncAltIcon/>}> {/* TODO setup onclick */}
              Refresh 
            </Button>
          </ToolbarItem>
        </ToolbarGroup>
      </React.Fragment>
    );

    const ToolbarItems = (
      <ToolbarToggleGroup toggleIcon={<FilterIcon />} breakpoint="xl">
        {toggleGroupItems}
      </ToolbarToggleGroup>
    );

    const panelContent = (
      <DrawerPanelContent>
        <DrawerHead>
          <Title headingLevel="h2" size="xl">
            Details
            {/* node-{selectedDataListItemId} */}
          </Title>
          <DrawerActions>
            <DrawerCloseButton onClick={onCloseClick} />
          </DrawerActions>
        </DrawerHead>
        <DrawerPanelBody>
          <Flex spaceItems={{ default: 'spaceItemsLg' }} direction={{ default: 'column' }}>
            <FlexItem>
              <p>
              Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum
              </p>
            </FlexItem>
          </Flex>
        </DrawerPanelBody>
      </DrawerPanelContent>
    );

    const advicesToDataListItem = (
      <>
      {advices.map(a => (
          <DataListItem id={"content-padding-item"+a.title}>
              <DataListItemRow>
              <DataListItemCells
                dataListCells={[
                  <DataListCell key="primary content">
                    <Flex direction={{ default: 'column' }}>
                      <FlexItem>
                        <p>{a.title}</p>
                        <small>
                          {a.description}
                        </small>
                      </FlexItem>
                      <Flex>
                        <FlexItem>
                          <CodeIcon /> 1
                        </FlexItem>
                        <FlexItem>
                          <ExclamationTriangleIcon /> 1
                        </FlexItem>
                      </Flex>
                    </Flex>
                  </DataListCell>,
                  <DataListAction aria-labelledby="single-action-item1 single-action-action1"
                  id="single-action-action1"
                  aria-label="Actions">
                    <Stack>
                      <StackItem>
                        <Button variant={ButtonVariant.secondary}>Details</Button>
                      </StackItem>
                      <StackItem>
                        <Button variant={ButtonVariant.link}>About</Button>
                      </StackItem>
                    </Stack>
                  </DataListAction>
                ]}
              />
            </DataListItemRow>
          </DataListItem>
      ))}
      </>
    );

    const mockedCell = ([
      <DataListCell key="primary content">
        <Flex direction={{ default: 'column' }}>
          <FlexItem>
            <p><Skeleton width="33%" /></p><br />
            <small>
              <Skeleton width="45%" />
              <Skeleton width="40%" style={{marginTop: "10px"}}/>
            </small>
          </FlexItem>
        </Flex>
      </DataListCell>
    ]);
    const mockedAdvisesDataListItems = (
      <>
        <DataListItem id="content-padding-item-1">
          <DataListItemRow>
            <DataListItemCells
              dataListCells={mockedCell}
            />
          </DataListItemRow>
        </DataListItem>
        <DataListItem id="content-padding-item-2">
          <DataListItemRow>
            <DataListItemCells
              dataListCells={mockedCell}
            />
          </DataListItemRow>
        </DataListItem>
      </>
    );

    const drawerContent = (
      <React.Fragment>
        <Toolbar id="content-padding-data-toolbar" usePageInsets>
          <ToolbarContent>{ToolbarItems}</ToolbarContent>
        </Toolbar>
        <DataList
          aria-label="data list"
          selectedDataListItemId={selectedDataListItemId}
          onSelectDataListItem={onSelectDataListItem}
        >
          {foundAdvices ? advicesToDataListItem : mockedAdvisesDataListItems}
        </DataList>
      </React.Fragment>
    );

    return (
      <>
        {/* <PageSection variant={PageSectionVariants.light}>
          <TextContent>
            <Text component="h1">Main Dashboard</Text>
            <Text component="p">
              Using this dashboard you can overview the advices emitted by RuleOps,<br />
              using internally the Drools rule engine while inspecting your Kubernetes cluster.
            </Text>
          </TextContent>
        </PageSection>
        <Divider component="div" /> */}
        <PageSection padding={{ default: 'noPadding' }}>
          <Drawer isExpanded={isExpanded} onExpand={onExpand}>
            <DrawerContent panelContent={panelContent} className={'pf-m-no-background'}>
              <DrawerContentBody hasPadding>{drawerContent}</DrawerContentBody>
            </DrawerContent>
          </Drawer>
        </PageSection>
      </>
    );

}
