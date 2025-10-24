# Capacity considerations

Short answer: plan for bandwidth and CPUs, not “JVBs per participant.” One Jitsi Videobridge (JVB) can handle many participants 
if you control bitrate and SFU load. Use autoscaling and shard across multiple JVBs for resilience.
Practical sizing guidelines (starting point):
- Per participant media budget (typical constraints mode, VP8, 30 fps):
- Uplink per sender: ~1.5–2.5 Mbps for HD video; ~300–600 Kbps for LD; audio ~40–60 Kbps.
- Downlink per participant in a 1 active speaker + thumbnails layout: ~1.0–2.5 Mbps.

## CPU per participant on JVB:
Rough rule: ~15–30 mCPU per actively sending video stream on modern x86 servers with no transcoding (JVB forwards packets). 
Plan ~200–400 active video senders per 8 vCPU node before hitting limits, depending on bitrate, RTP packet rate, and network.

Conference size targets per JVB (conservative):
- Small meetings (<=10 participants): ~60–100 concurrent users per 4 vCPU JVB.
- Medium (10–25 participants): ~40–80 users per 8 vCPU JVB.
- Large webinars (one or few senders, many viewers): 300–600 viewers per 8–16 vCPU JVB. These vary widely with codecs, 
bitrates, simulcast layers, and packet loss.

How many JVBs?
- Start with 2 JVBs for HA. Add more based on concurrent participants and target utilization (keep <60–70% CPU and <70% NIC).
- As a rough initial capacity assumption: 1 JVB (8 vCPU, 8–16 GB RAM, 10 Gbps NIC) ≈ 300–500 concurrent participants across 
many small meetings with simulcast and constrained video. Validate with load tests.
- Use Octo (region-aware) if you span regions; otherwise one region with several JVBs behind the Jicofo load assignment is fine.

### Key levers to increase density:
- Enable simulcast and LastN to reduce downstream load.
- Limit max resolution/frame rate (e.g., cap to 720p or 360p for classes).
- Prefer VP9 SVC (if your client mix supports it) for better efficiency; otherwise VP8 simulcast.
- Turn on congestion control and set sensible max bitrates in config.
- Keep PSTN/SIP and recording (Jibri) separate from JVB capacity planning.
- Jibri (recording/streaming):
  - 1 Jibri per concurrent recording/stream. Budget CPU/GPU accordingly (1080p needs more).
- Capacity planning workflow:
  - Define target layout and bitrates (e.g., teacher HD, students LD).
  - Run a synthetic load test (SIP/RTC bots or staged users) to measure CPU, NIC, and packet loss on a JVB.
  - Set utilization limits and derive participants-per-JVB for your scenario.
  - Add a 30–50% headroom. Autoscale JVBs on queue depth/CPU/ conferences count.
  - Monitor: JVB CPU, pps, packet loss, RTT, conference count, endpoints sending/receiving, bitrate per layer.

## Glossary
SFU stands for Selective Forwarding Unit.
- It receives media streams from participants and forwards selected streams to others without decoding/re-encoding.
- In Jitsi, the Jitsi Videobridge (JVB) is the SFU. It routes simulcast/SVC layers based on bandwidth and layout (e.g., LastN), 
which scales better than MCU (which mixes/transcodes).

## Historic stats

- Tutoring Session Hrs. for the 2025-2025 School Year (Aug. --> Aug.): 2,191

- Tutoring Session Hrs. for the 2025-2026 School Year to Date (Aug. --> Present): 380

- Class Session Hrs. for the 2024-2025 School Year: 3,240

- Estimated Class Session Hrs. for the 2025-2026 School Year: 3,450

- Misc. Hrs. (meetings, special sessions, etc.) for a Typical School Year: 150

- Summer Programs (Summer 2025): 55
