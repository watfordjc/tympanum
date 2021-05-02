# tympanum
Potential WIP: geoloc to words algorithm

## Why the name Tympanum?

A **tympanum** is a decorative arch over the entrance to a building. It is also the name of a Roman drum (alternative spelling: **tympanon**). It is also related to listening, with parts of humans (eardrum), frogs (hearing structure), and insects (hearing organ) all being given the biological name tympanum.

A **tympan** (in the context of astrolabes) is a swappable plate, each designed for a specific latitude. 

* **Drum and Bass** is Cockney rhyming slang for **Place**. It is often shortened to **Drum**.
* **Gates of Rome** (and other variations also using Rome) is Cockney rhyming slang for **Home**.
* A typanum was a Drum in Rome.

GPS is based on numbers, and many keyboards shorten the number lock key to **num**. The French word for no is **non**. GPS receivers listen to the signals from GNSS satellites (if they can hear them).

Why the name Tympanum? Wordplay.

## IARU Grid Locator (Maidenhead Locator System)

There are various grid systems in use for locating somewhere, and I came across the Maidenhead system when I was watching FT8 contacts on ham radio (via a receive-only software-defined radio). There were CQs giving a four character code at the end in the format of two letters followed by two numbers, and I wondered what they meant.

The way it works is there are pairs of characters. Two letters (AA-RR), followed by two numbers (00-99). Extra levels of precision are created by adding more character pairs alternating between letters (AA-XX) and numbers (00-99). Beyond 8 characters was standardised by the IARU in 2019. Since 1999 it has used WGS 84, the geodetic system used by GPS.

Each pair of characters defines a grid squaroid, starting at the bottom left of the previous square. The first two characters represent the **field**, with AA being at 180 degrees west to 160 degrees west, 90 degrees south to 80 degrees south. RR would be 160 degrees east to 180 degrees east, 80 degrees north to 90 degrees north.

The field is followed by two numbers, called the **square**, dividing the field into a 10x10 grid with each square being 2 degrees of longitude (20/10) by 1 degree of latitude (10/10). The bottom left square is 00, the top left 09, the bottom right 90, and the top right 99.

The square is followed by the **subsquare**, two letters in the range AA-XX (which might be represented aa-xx by older software). This splits the square into a 24x24 grid with each square being 5 minutes of longitude (2/24\*60) and 2.5 minutes of latitude (1/24\*60). The bottom left subsquare is AA, the top right is XX.

The subsquare is followed by the **extended square**, dividing the subsquare into a 10x10 grid. Each extended square is 30 seconds of longitude (5/10\*60) and 15 seconds of latitude (2.5/10\*60).

Repeating the rules for subsquares and extended squares, the next pair of characters are in the range AA-XX, splitting the extended square into a 24x24 grid. This **extended subsquare** is 1.25 seconds of longitude (30/24) by 0.625 seconds of latitude (15/24).

Split the extended subsquare into a 10x10 grid, and you have a grid of **extended extended square**s with each one 0.125 seconds of longitude by 0.00625 seconds of latitude. 0.0625 arc seconds of latitude is about 1.9 metres. 0.125 arc seconds of longitude is about 0-4 metres (zero at the poles, 3.85m at the equator). At the equator, two people in the same extended extended square and at the same altitude will be within 4.7 metres of each other. At 51 degrees latitude, they'll be within 3.1 metres.

Technically, the position for each level of detail is for the centre point of the squaroid. 12 IARU Grid Locator characters can get you within 4.7 metres of somewhere/someone, and within 2.4 metres of that centre point. With higher accuracy and swinging your phone around a few seconds to calibrate the compass, you could use two fixed points (the centre point and a more accurate position of the other person) to work out the bearing and distance of the other person. Of course, if you both spin on your spots with your arms stretched out in that extended extended square, you might be close enough to hit each other (assuming same altitude).

## Encoding IARU Grid Location

I'm not too sure how FT8 does it, but it uses 15 bits for the 4 character location.

Using A=0 and R=17, and 00-99...

```
AB15 = (0*18 + 1) * 100 + 15 = 115, or 01110011 in 8 bits.
115 % 100 = 15
(115-15) / 100 = 1
1 % 18 = 1 = B
(1-1) / 18 = 0 = A

RR99 = (17*18 + 17) * 100 + 99 = 32,399, or 111111010001111 in 15 bits.
32,399 % 100 = 99
(32,399 - 99) / 100 = 323
323 % 18 = 17 = R
(323 - 17) / 18 = 17 = R
```

There is a question of how to pack the bits and how to represent them. As I already plan to use BIP 0039 for LTO tape encryption, the English wordlist for Bitcoin mnemonics makes the most sense, followed by the PGP word list. As BIP 0039 uses 2048 words and PGP uses 256, BIP 0039 will use fewer words.

### BIP 0039

BIP 0039 uses 11-bit words. Extending it for use with fewer bytes, I need to create a multiple of 32 bits of data then use the spare bits for the checksum.

Whilst I was tired, I went with the following for a location of AA00BB11CC22:
* 31 bits: AA00BB11
* 16 bits: CC22
* 1 bit: GNSS lock
* 1 bit: altitude sign bit
* 15 bits: altitude/depth in metres
* 2 bits: checksum

After sleeping on it, I realised the 31 bits would take up the same space in 15+16 bits, so I've now settled on the following:
* 15 bits: AA00
* 16 bits: BB11
* 16 bits: CC22
* 1 bit: GNSS lock
* 1 bit: altitude sign bit
* 15 bits: altitude/depth in metres
* 2 bits: checksum

There will be some overlap between squaroids because the words are 11 bits. For example, the first word will contain less data than the first squaroid location.

My current location is in square IO91.

```
IO91 = (8*18 + 14) * 100 + 91 = 15,891, or 011111000010011

011111000010000 = 15,888
011111000011111 = 15,903
15,903 - 15,888 + 1 = 16 squares

15,888 % 100 = 88
(15,888 - 88) / 100 = 158
158 % 18 = 14 = O
(158 - 14) / 18 = 8 = I
= IO88

15,903 % 100 = 3
(15,903 - 3) / 100 = 159
159 % 18 = 15 = P
(159 - 15) / 18 = 8 = I
= IP03
```

The first word is not enough. IO88 has John O'Groats at the top of Great Britain in its centre, IO89 covers the northern half of Orkney, IO90 contains the Isle of Man and Portsmouth, IO91-IO94 covers London to the Cotswolds from just north of Southampton to Newcastle, and IO95 has a chunk of Northumberland and quite a bit of North Sea. IO99 has the southern tip of the Shetlands. IP03 has the southern point of Iceland, and IP02-IP00 are south of that in the North Atlantic Ocean.

This does, however, mean that assumptions can be made. IO88-IP03 start with "label". JO91 containing East London is to the east of IO91 (which contains West London) and is 0100010100011011, so the first word covers 17,680 to 17,695. 010001010001 is 1,105, with the 1,106th BIP 0039 word being "mechanic". The first word will only change when crossing the border of a square, with the new square not having the same upper 11 bits as the one you were previously in. The word doesn't represent a contiguous area.

The first two words will contain the full data for the square, and the upper 7 bits of the extended subsquare and subsquare.

```
TP35 = (19*24 + 15) * 100 + 35 = 47,135 or 1011100000011111 in 16 bits

10111000 00000000 = 47,104
10111001 11111111 = 47,615
47,615 - 47,104 + 1 = 512 extended subsquares

47,104 % 100 = 04
(47,104 - 4) / 100 = 471
471 % 24 = 15 = P
(471 - 15) / 24 = 19 = T

47,615 % 100 = 15
(47,615 - 15) / 100 = 476
476 % 24 = 20 = U
(476 - 20) / 24 = 19 = T
```

So, **label describe** will give a location in IO91 that is somewhere in IO91TP04 through IO91TU15. Again, that might not be a contiguous area. Unless you're near the border of a field/square/subsquare, you can probably get more than this level of accuracy from a mobile phone mast.

Four words will give a level of accuracy to about 4 parking spaces. Five words will give the desired 2D position (i.e. within 4.7 metres of somewhere, and within 2.4 of the centre point), whether GPS is locked (may need to define that as a desired accuracy level), whether above or below sea level, and whether at an extreme altitude/depth. Six words will additionally give the approximate altitude/depth in metres (from the crust to the lower stratosphere), and a 2 bit checksum (very small sanity check).

I did consider greater accuracy, but GNSS "locations" can jump around a bit which would cause the words to be very unstable. Wi-Fi FMC RTT (part of 802.11mc) doesn't seem to be getting widespread support, and I haven't been keeping track of new Bluetooth features/standards or GNSS phase shift measurement technology. 1 metre accuracy is possible, but the phone chips, firmware, and infrastructure appear to be rare at present. 
 
### Altitude

The altitude takes up 16 bits because I was trying to balance accuracy with the rules of BIP 0039. Since 47 bits is not a multiple of 32, the next possibility was 64 bits, giving me 17 bits to play with. I had been playing around with building floor numbers and flight levels, but then thought about how the bits being split between words would work.

Both 1's compliment and 2's compliment do not put the important bits first. If you're below ground level or in the sky, the extremes should fit in one word. Thus, there is a sign bit, followed by a regular unsigned 15 bit number. Sea level is positive zero, negative zero is unused.

The fifth word will contain the sign bit and the upper 7 bits of altitude/depth. That covers altitudes/depths of more than 255 metres up to +/- 32,767 to the nearest (rounded towards sea level) 256 metres. 32,767 metres is 107,503 feet - planes might fly at 40,000 feet if there are no passengers, the Mariana Trench's maximum known depth is 36,037 feet. I could've gone with feet, but metres seemed sufficient enough to give an approximate floor number in a building.
