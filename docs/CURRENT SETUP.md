
## Notes on current setup

- Videobridges located on separate servers together with one Jibri node each.
- Videobridges are connected to the main instance, the config for that can be found at /etc/jitsi/videobridge.
  Connection details inside jvb.conf
- Videobridges are configured with custom IDs in order to make websocket connection working. Serverid is configured in
  jvb.conf (located in "/etc/jitsi/videobridge" for each JVB server), the corresponding endpoint is configured in nginx
  config at /etc/nginx/sites-enabled/ [I believe this is referring to the main node, not each JVB] for colibri-ws
- Custom configuration were made to prosody, mainly the token authentication module. This can be found in the configured
  prosody path at /etc/prosody/conf.d/xxx.conf, prosody-plugins path. Probably mod_token_authentication.lua
- JWT token moderation plugin is used for prosody in order to enable the moderator: true flag in the JWT token to assign
  moderator privileges inside a meeting
- Config files for Jitsi are located in /etc/jitsi/meet to configure authdomain etc. [This is on the main node.]

At the beginning of the project we had issues with the websocket and bosh connection, websocket is more favourable than
bosh and bosh is only used when websocket is not working. For websocket to work, it is important that each videobridge
has a server-id configured in jvb.conf and that this id has its own endpoint in the nginx config, so the websocket
request gets routed properly to the correct bridge. So the IP of the bridge also needs to be set in the location block (nginx config)

We also made custom configuration to the Jibri module, to check the path for the Jibri .jar check /etc/systemd/system/jibri.service. So I would not upgrade Jibri else the custom version will be overwritten.

- Videobridges located on seperate servers together with one Jibri node each
- Videobridges are connected to the main instance, the config for that can be found at /etc/jitsi/videobridge. Connection details inside jvb.conf
- Videobridges are configured with custom IDs in order to make websocket connection working. Serverid is configured in jvb.conf (located in "/etc/jitsi/videobridge" for each JVB server), the corresponding endpoint is configured in nginx config at /etc/nginx/sites-enabled/ [I believe this is referring to the main node, not each JVB] for colibri-ws
- Custom configuration were made to prosody, mainly the token authentication module. This can be found in the configured prosody path at /etc/prosody/conf.d/xxx.conf, prosody-plugins path. Probably mod_token_authentication.lua
- JWT token moderation plugin is used for prosody in order to enable the moderator: true flag in the JWT token to assign moderator privileges inside a meeting
- Config files for Jitsi are located in /etc/jitsi/meet to configure authdomain etc. [This is on the main node.]

jvb == videobridge
jibri == session recording
prosody == XMPP signalling
jacofo == conference focus:media sessions and load balancer between each participant and the video bridge
jitsi meet == javascript WebRtc client
jigasi == SIP gateway to allow sip clients to connect.
