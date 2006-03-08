/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.slick.contactlist.mockprovider;

import java.util.*;

import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.service.protocol.event.*;
import net.java.sip.communicator.util.*;
import net.java.sip.communicator.service.protocol.icqconstants.*;

/**
 * A mock implementation of a persistent presence operation set containing a
 * constant contact list and used for testing the meta contact list.
 * @author Emil Ivov
 */
public class MockPersistentPresenceOperationSet
    implements OperationSetPersistentPresence
{
    private static final Logger logger =
        Logger.getLogger(MockPersistentPresenceOperationSet.class);
    /**
     * A list of listeners registered for <tt>SubscriptionEvent</tt>s.
     */
    private Vector subscriptionListeners = new Vector();

    /**
     * A list of listeners registered for <tt>ServerStoredGroupChangeEvent</tt>s.
     */
    private Vector serverStoredGroupListeners = new Vector();

    /**
     * The root of the mock contact list.
     */
    private MockContactGroup contactListRoot = null;

    /**
     * The provider that created us.
     */
    private MockProvider parentProvider = null;

    /**
     * The currently active status message.
     */
    private String statusMessage = null;

    public MockPersistentPresenceOperationSet(MockProvider provider)
    {
        this.parentProvider = provider;
        contactListRoot = new MockContactGroup("RootMockGroup", provider);
    }

    /**
     * Mock implementation of the corresponding ProtocolProviderService method.
     *
     * @param listener a dummy param.
     */
    public void addContactPresenceStatusListener(ContactPresenceStatusListener
                                                 listener)
    {

    }

    /**
     * Notifies all registered listeners of the new event.
     *
     * @param source the contact that has caused the event.
     * @param parentGroup the group that contains the source contact.
     * @param eventID an identifier of the event to dispatch.
     */
    public void fireSubscriptionEvent(MockContact  source,
                                      ContactGroup parentGroup,
                                      int          eventID)
    {
        SubscriptionEvent evt  = new SubscriptionEvent(source, this.parentProvider,
                    parentGroup, eventID);
        for ( int i = 0; i < subscriptionListeners.size(); i++ )
        {
            SubscriptionListener listener = (SubscriptionListener)
                subscriptionListeners.get(i);
            if(eventID == SubscriptionEvent.SUBSCRIPTION_CREATED)
            {
                listener.subscriptionCreated(evt);
            }
            else if (eventID == SubscriptionEvent.SUBSCRIPTION_FAILED)
            {
                listener.subscriptionFailed(evt);
            }
            else if (eventID == SubscriptionEvent.SUBSCRIPTION_REMOVED)
            {
                listener.subscriptionRemoved(evt);
            }
        }
    }

    /**
     * Notifies all registered listeners of the new event.
     *
     * @param source the contact that has caused the event.
     * @param eventID an identifier of the event to dispatch.
     */
    public void fireServerStoredGroupEvent(MockContactGroup  source,
                                           int               eventID)
    {
        ServerStoredGroupEvent evt  = new ServerStoredGroupEvent(
            source, eventID, this.parentProvider, this);

        for ( int i = 0; i < serverStoredGroupListeners.size(); i++ )
        {
            ServerStoredGroupListener listener = (ServerStoredGroupListener)
                serverStoredGroupListeners.get(i);
            if(eventID == ServerStoredGroupEvent.GROUP_CREATED_EVENT)
            {
                listener.groupCreated(evt);
            }
            else if(eventID == ServerStoredGroupEvent.GROUP_RENAMED_EVENT)
            {
                listener.groupNameChanged(evt);
            }
            else if(eventID == ServerStoredGroupEvent.GROUP_REMOVED_EVENT)
            {
                listener.groupRemoved(evt);
            }
        }
    }


    /**
     * Mock implementation of the corresponding ProtocolProviderService method.
     *
     * @param listener a dummy param.
     */
    public void addProviderPresenceStatusListener(
        ProviderPresenceStatusListener listener)
    {
    }

    /**
     * Registers a listener that would receive events upon changes in server
     * stored groups.
     *
     * @param listener a ServerStoredGroupChangeListener impl that would
     *   receive events upong group changes.
     */
    public void addServerStoredGroupChangeListener(ServerStoredGroupListener
                                                        listener)
    {
        serverStoredGroupListeners.add(listener);
    }

    /**
     * Mock implementation of the corresponding ProtocolProviderService method.
     *
     * @param listener the SubscriptionListener to register
     */
    public void addSubsciptionListener(SubscriptionListener listener)
    {
        this.subscriptionListeners.add( listener );
    }

    /**
     * Creates a group with the specified name and parent in the server
     * stored contact list.
     *
     * @param parent the group where the new group should be created
     * @param groupName the name of the new group to create.
     */
    public void createServerStoredContactGroup(ContactGroup parent,
                                               String groupName)
    {
        MockContactGroup newGroup
            = new MockContactGroup(groupName, parentProvider);

        ((MockContactGroup)parent).addSubGroup(newGroup);

        this.fireServerStoredGroupEvent(
            newGroup, ServerStoredGroupEvent.GROUP_CREATED_EVENT);
    }

    /**
     * A Mock Provider method to use for fast filling of a contact list.
     *
     * @param contactGroup the group to add
     */
    public void addMockGroup(MockContactGroup contactGroup)
    {
        contactListRoot.addSubGroup(contactGroup);
    }

    /**
     * Returns a reference to the contact with the specified ID in case we
     * have a subscription for it and null otherwise/
     *
     * @param contactID a String identifier of the contact which we're
     *   seeking a reference of.
     * @return a reference to the Contact with the specified
     *   <tt>contactID</tt> or null if we don't have a subscription for the
     *   that identifier.
     */
    public Contact findContactByID(String contactID)
    {
        return contactListRoot.findContactByID(contactID);
    }

    /**
     * Sets the specified status message.
     * @param statusMessage a String containing the new status message.
     */
    public void setStatusMessage(String statusMessage)
    {
        this.statusMessage = statusMessage;
    }

    /**
     * Returns the status message that was last set through
     * setCurrentStatusMessage.
     *
     * @return the last status message that we have requested and the aim
     *   server has confirmed.
     */
    public String getCurrentStatusMessage()
    {
        return statusMessage;
    }

    /**
     * Returns the protocol specific contact instance representing the local
     * user.
     *
     * @return the Contact (address, phone number, or uin) that the Provider
     *   implementation is communicating on behalf of.
     */
    public Contact getLocalContact()
    {
        return null;
    }

    /**
     * Returns a PresenceStatus instance representing the state this provider
     * is currently in.
     *
     * @return the PresenceStatus last published by this provider.
     */
    public PresenceStatus getPresenceStatus()
    {
        return IcqStatusEnum.ONLINE;
    }

    /**
     * Returns the root group of the server stored contact list.
     *
     * @return the root ContactGroup for the ContactList stored by this
     *   service.
     */
    public ContactGroup getServerStoredContactListRoot()
    {
        return contactListRoot;
    }

    /**
     * Returns the set of PresenceStatus objects that a user of this service
     * may request the provider to enter.
     *
     * @return Iterator a PresenceStatus array containing "enterable" status
     *   instances.
     */
    public Iterator getSupportedStatusSet()
    {
        return IcqStatusEnum.icqStatusSet.iterator();
    }

    /**
     * Removes the specified contact from its current parent and places it
     * under <tt>newParent</tt>.
     *
     * @param contactToMove the <tt>Contact</tt> to move
     * @param newParent the <tt>ContactGroup</tt> where <tt>Contact</tt>
     *   would be placed.
     */
    public void moveContactToGroup(Contact contactToMove,
                                   ContactGroup newParent)
    {
        /** @todo implement moveContactToGroup() */
    }

    /**
     * Requests the provider to enter into a status corresponding to the
     * specified paramters.
     *
     * @param status the PresenceStatus as returned by
     *   getRequestableStatusSet
     * @param statusMessage the message that should be set as the reason to
     *   enter that status
     * @throws IllegalArgumentException if the status requested is not a
     *   valid PresenceStatus supported by this provider.
     * @throws IllegalStateException if the provider is not currently
     *   registered.
     * @throws OperationFailedException with code NETWORK_FAILURE if
     *   publishing the status fails due to a network error.
     */
    public void publishPresenceStatus(PresenceStatus status,
                                      String statusMessage) throws
        IllegalArgumentException, IllegalStateException,
        OperationFailedException
    {
        /** @todo implement publishPresenceStatus() */
    }

    /**
     * Get the PresenceStatus for a particular contact.
     *
     * @param contactIdentifier the identifier of the contact whose status
     *   we're interested in.
     * @return PresenceStatus the <tt>PresenceStatus</tt> of the specified
     *   <tt>contact</tt>
     * @throws IllegalArgumentException if <tt>contact</tt> is not a contact
     *   known to the underlying protocol provider
     * @throws IllegalStateException if the underlying protocol provider is
     *   not registered/signed on a public service.
     * @throws OperationFailedException with code NETWORK_FAILURE if
     *   retrieving the status fails due to errors experienced during
     *   network communication
     */
    public PresenceStatus queryContactStatus(String contactIdentifier) throws
        IllegalArgumentException, IllegalStateException,
        OperationFailedException
    {
        return IcqStatusEnum.ONLINE;
    }

    /**
     * Removes the specified listener so that it won't receive any further
     * updates on contact presence status changes
     *
     * @param listener the listener to remove.
     */
    public void removeContactPresenceStatusListener(
        ContactPresenceStatusListener listener)
    {
    }

    /**
     * Unregisters the specified listener so that it does not receive further
     * events upon changes in local presence status.
     *
     * @param listener ProviderPresenceStatusListener
     */
    public void removeProviderPresenceStatusListener(
        ProviderPresenceStatusListener listener)
    {
        /** @todo implement removeProviderPresenceStatusListener() */
    }

    /**
     * Returns the group that is parent of the specified mockGroup  or null
     * if no parent was found.
     * @param mockGroup the group whose parent we're looking for.
     * @return the MockContactGroup instance that mockGroup belongs to or null
     * if no parent was found.
     */
    public MockContactGroup findGroupParent(MockContactGroup mockGroup)
    {
        return contactListRoot.findGroupParent(mockGroup);
    }

    /**
     * Removes the specified group from the server stored contact list.
     *
     * @param group the group to remove.
     *
     * @throws IllegalArgumentException if <tt>group</tt> was not found in this
     * protocol's contact list.
     */
    public void removeServerStoredContactGroup(ContactGroup group)
        throws IllegalArgumentException
    {
        MockContactGroup mockGroup = (MockContactGroup)group;

        MockContactGroup parent = findGroupParent(mockGroup);

        if(parent == null){
            throw new IllegalArgumentException(
                "group " + group
                + " does not seem to belong to this protocol's contact list.");
        }

        parent.removeSubGroup(mockGroup);

        this.fireServerStoredGroupEvent(
            mockGroup, ServerStoredGroupEvent.GROUP_REMOVED_EVENT);
    }


    /**
     * Removes the specified group change listener so that it won't receive
     * any further events.
     *
     * @param listener the ServerStoredGroupChangeListener to remove
     */
    public void removeServerStoredGroupChangeListener(ServerStoredGroupListener
        listener)
    {
        serverStoredGroupListeners.remove(listener);
    }

    /**
     * Removes the specified subscription listener.
     *
     * @param listener the listener to remove.
     */
    public void removeSubsciptionListener(SubscriptionListener listener)
    {
        this.subscriptionListeners.remove(listener);
    }

    /**
     * Renames the specified group from the server stored contact list.
     *
     * @param group the group to rename.
     * @param newName the new name of the group.
     */
    public void renameServerStoredContactGroup(ContactGroup group,
                                               String newName)
    {
        ((MockContactGroup)group).setGroupName(newName);

        this.fireServerStoredGroupEvent(
            (MockContactGroup)group, ServerStoredGroupEvent.GROUP_RENAMED_EVENT);
    }

    /**
     * Handler for incoming authorization requests.
     *
     * @param handler an instance of an AuthorizationHandler for
     *   authorization requests coming from other users requesting
     *   permission add us to their contact list.
     */
    public void setAuthorizationHandler(AuthorizationHandler handler)
    {
        /** @todo implement setAuthorizationHandler() */
    }

    /**
     * Persistently adds a subscription for the presence status of the
     * contact corresponding to the specified contactIdentifier and indicates
     * that it should be added to the specified group of the server stored
     * contact list.
     *
     * @param parent the parent group of the server stored contact list
     *   where the contact should be added. <p>
     * @param contactIdentifier the contact whose status updates we are
     *   subscribing for.
     * @throws IllegalArgumentException if <tt>contact</tt> or
     *   <tt>parent</tt> are not a contact known to the underlying protocol
     *   provider.
     * @throws IllegalStateException if the underlying protocol provider is
     *   not registered/signed on a public service.
     * @throws OperationFailedException with code NETWORK_FAILURE if
     *   subscribing fails due to errors experienced during network
     *   communication
     */
    public void subscribe(ContactGroup parent, String contactIdentifier) throws
        IllegalArgumentException, IllegalStateException,
        OperationFailedException
    {
        MockContact contact = new MockContact(contactIdentifier
                                              , parentProvider
                                              , (MockContactGroup)parent);

        ((MockContactGroup)parent).addContact(contact);

        fireSubscriptionEvent(contact,
                                       parent,
                                       SubscriptionEvent.SUBSCRIPTION_CREATED);

    }

    /**
     * Adds a subscription for the presence status of the contact
     * corresponding to the specified contactIdentifier.
     *
     * @param contactIdentifier the identifier of the contact whose status
     *   updates we are subscribing for. <p>
     * @throws IllegalArgumentException if <tt>contact</tt> is not a contact
     *   known to the underlying protocol provider
     * @throws IllegalStateException if the underlying protocol provider is
     *   not registered/signed on a public service.
     * @throws OperationFailedException with code NETWORK_FAILURE if
     *   subscribing fails due to errors experienced during network
     *   communication
     */
    public void subscribe(String contactIdentifier) throws
        IllegalArgumentException, IllegalStateException,
        OperationFailedException
    {
        MockContact contact = new MockContact(contactIdentifier
                                              , parentProvider
                                              , contactListRoot);

        contactListRoot.addContact(contact);

        fireSubscriptionEvent(contact,
                              contactListRoot,
                              SubscriptionEvent.SUBSCRIPTION_CREATED);

    }

    /**
     * Removes a subscription for the presence status of the specified
     * contact.
     *
     * @param contact the contact whose status updates we are unsubscribing
     *   from.
     * @throws IllegalArgumentException if <tt>contact</tt> is not a contact
     *   known to the underlying protocol provider
     * @throws IllegalStateException if the underlying protocol provider is
     *   not registered/signed on a public service.
     * @throws OperationFailedException with code NETWORK_FAILURE if
     *   unsubscribing fails due to errors experienced during network
     *   communication
     */
    public void unsubscribe(Contact contact) throws IllegalArgumentException,
        IllegalStateException, OperationFailedException
    {
        ((MockContact)contact).getParentGroup()
            .removeContact((MockContact)contact);

        fireSubscriptionEvent((MockContact)contact,
                                       ((MockContact)contact).getParentGroup(),
                                       SubscriptionEvent.SUBSCRIPTION_REMOVED);
    }
}
