PGDMP  (                    }           pcbuildingapp    17.4    17.4 -    I           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                           false            J           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                           false            K           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                           false            L           1262    16388    pcbuildingapp    DATABASE     s   CREATE DATABASE pcbuildingapp WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en-US';
    DROP DATABASE pcbuildingapp;
                     postgres    false            M           0    0    DATABASE pcbuildingapp    COMMENT     J   COMMENT ON DATABASE pcbuildingapp IS 'PC Building App project database.';
                        postgres    false    4940            N           0    0    DATABASE pcbuildingapp    ACL     ;   GRANT ALL ON DATABASE pcbuildingapp TO pcbuildingapp_user;
                        postgres    false    4940                        2615    16389    pcbuildingappschema    SCHEMA     #   CREATE SCHEMA pcbuildingappschema;
 !   DROP SCHEMA pcbuildingappschema;
                     postgres    false            O           0    0    SCHEMA pcbuildingappschema    COMMENT     K   COMMENT ON SCHEMA pcbuildingappschema IS 'PC Building App project schema';
                        postgres    false    5            P           0    0    SCHEMA pcbuildingappschema    ACL     ?   GRANT ALL ON SCHEMA pcbuildingappschema TO pcbuildingapp_user;
                        postgres    false    5            �            1259    16391    useraccount    TABLE     �   CREATE TABLE pcbuildingappschema.useraccount (
    accountid integer NOT NULL,
    username character varying(32) NOT NULL,
    password character varying(128) NOT NULL
);
 ,   DROP TABLE pcbuildingappschema.useraccount;
       pcbuildingappschema         heap r       postgres    false    5            Q           0    0    TABLE useraccount    ACL     J   GRANT ALL ON TABLE pcbuildingappschema.useraccount TO pcbuildingapp_user;
          pcbuildingappschema               postgres    false    218            �            1259    16390    UserAccount_accountID_seq    SEQUENCE     �   CREATE SEQUENCE pcbuildingappschema."UserAccount_accountID_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 ?   DROP SEQUENCE pcbuildingappschema."UserAccount_accountID_seq";
       pcbuildingappschema               postgres    false    5    218            R           0    0    UserAccount_accountID_seq    SEQUENCE OWNED BY     s   ALTER SEQUENCE pcbuildingappschema."UserAccount_accountID_seq" OWNED BY pcbuildingappschema.useraccount.accountid;
          pcbuildingappschema               postgres    false    217            S           0    0 $   SEQUENCE "UserAccount_accountID_seq"    ACL     ]   GRANT ALL ON SEQUENCE pcbuildingappschema."UserAccount_accountID_seq" TO pcbuildingapp_user;
          pcbuildingappschema               postgres    false    217            �            1259    16428    pcbuildlist    TABLE     Q  CREATE TABLE pcbuildingappschema.pcbuildlist (
    pcbuildlistid integer DEFAULT nextval('pcbuildingappschema."UserAccount_accountID_seq"'::regclass) NOT NULL,
    accountid integer NOT NULL,
    pcbuildlistname character varying(128) NOT NULL,
    description character varying(255),
    creationdate date NOT NULL,
    modifieddate date NOT NULL,
    totalprice numeric(8,2) DEFAULT 0.0 NOT NULL,
    listtype character varying(64),
    content character varying(64),
    category character varying(64),
    thumbnailurl character varying(255),
    publish boolean DEFAULT false NOT NULL
);
 ,   DROP TABLE pcbuildingappschema.pcbuildlist;
       pcbuildingappschema         heap r       postgres    false    217    5            T           0    0    TABLE pcbuildlist    ACL     J   GRANT ALL ON TABLE pcbuildingappschema.pcbuildlist TO pcbuildingapp_user;
          pcbuildingappschema               postgres    false    221            �            1259    16446    pcbuildlist_pcpart    TABLE     �   CREATE TABLE pcbuildingappschema.pcbuildlist_pcpart (
    listpartid integer NOT NULL,
    listid integer,
    partid integer,
    quantity integer DEFAULT 1 NOT NULL
);
 3   DROP TABLE pcbuildingappschema.pcbuildlist_pcpart;
       pcbuildingappschema         heap r       pcbuildingapp_user    false    5            �            1259    16445 !   pcbuildlist_pcpart_listpartid_seq    SEQUENCE     �   CREATE SEQUENCE pcbuildingappschema.pcbuildlist_pcpart_listpartid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 E   DROP SEQUENCE pcbuildingappschema.pcbuildlist_pcpart_listpartid_seq;
       pcbuildingappschema               pcbuildingapp_user    false    223    5            U           0    0 !   pcbuildlist_pcpart_listpartid_seq    SEQUENCE OWNED BY     �   ALTER SEQUENCE pcbuildingappschema.pcbuildlist_pcpart_listpartid_seq OWNED BY pcbuildingappschema.pcbuildlist_pcpart.listpartid;
          pcbuildingappschema               pcbuildingapp_user    false    222            �            1259    16403    pcpart    TABLE     )  CREATE TABLE pcbuildingappschema.pcpart (
    partid integer NOT NULL,
    partname character varying(255) NOT NULL,
    parttype character varying(64) NOT NULL,
    manufacturer character varying(64),
    model character varying(255),
    price numeric(7,2) NOT NULL,
    specifications jsonb
);
 '   DROP TABLE pcbuildingappschema.pcpart;
       pcbuildingappschema         heap r       postgres    false    5            V           0    0    TABLE pcpart    ACL     E   GRANT ALL ON TABLE pcbuildingappschema.pcpart TO pcbuildingapp_user;
          pcbuildingappschema               postgres    false    220            �            1259    16402    pcpart_partID_seq    SEQUENCE     �   ALTER TABLE pcbuildingappschema.pcpart ALTER COLUMN partid ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME pcbuildingappschema."pcpart_partID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            pcbuildingappschema               postgres    false    5    220            W           0    0    SEQUENCE "pcpart_partID_seq"    ACL     U   GRANT ALL ON SEQUENCE pcbuildingappschema."pcpart_partID_seq" TO pcbuildingapp_user;
          pcbuildingappschema               postgres    false    219            �           2604    16449    pcbuildlist_pcpart listpartid    DEFAULT     �   ALTER TABLE ONLY pcbuildingappschema.pcbuildlist_pcpart ALTER COLUMN listpartid SET DEFAULT nextval('pcbuildingappschema.pcbuildlist_pcpart_listpartid_seq'::regclass);
 Y   ALTER TABLE pcbuildingappschema.pcbuildlist_pcpart ALTER COLUMN listpartid DROP DEFAULT;
       pcbuildingappschema               pcbuildingapp_user    false    222    223    223            �           2604    16394    useraccount accountid    DEFAULT     �   ALTER TABLE ONLY pcbuildingappschema.useraccount ALTER COLUMN accountid SET DEFAULT nextval('pcbuildingappschema."UserAccount_accountID_seq"'::regclass);
 Q   ALTER TABLE pcbuildingappschema.useraccount ALTER COLUMN accountid DROP DEFAULT;
       pcbuildingappschema               postgres    false    218    217    218            D          0    16428    pcbuildlist 
   TABLE DATA           �   COPY pcbuildingappschema.pcbuildlist (pcbuildlistid, accountid, pcbuildlistname, description, creationdate, modifieddate, totalprice, listtype, content, category, thumbnailurl, publish) FROM stdin;
    pcbuildingappschema               postgres    false    221   ;:       F          0    16446    pcbuildlist_pcpart 
   TABLE DATA           _   COPY pcbuildingappschema.pcbuildlist_pcpart (listpartid, listid, partid, quantity) FROM stdin;
    pcbuildingappschema               pcbuildingapp_user    false    223   �:       C          0    16403    pcpart 
   TABLE DATA           u   COPY pcbuildingappschema.pcpart (partid, partname, parttype, manufacturer, model, price, specifications) FROM stdin;
    pcbuildingappschema               postgres    false    220   �;       A          0    16391    useraccount 
   TABLE DATA           Q   COPY pcbuildingappschema.useraccount (accountid, username, password) FROM stdin;
    pcbuildingappschema               postgres    false    218   H       X           0    0    UserAccount_accountID_seq    SEQUENCE SET     W   SELECT pg_catalog.setval('pcbuildingappschema."UserAccount_accountID_seq"', 49, true);
          pcbuildingappschema               postgres    false    217            Y           0    0 !   pcbuildlist_pcpart_listpartid_seq    SEQUENCE SET     ^   SELECT pg_catalog.setval('pcbuildingappschema.pcbuildlist_pcpart_listpartid_seq', 173, true);
          pcbuildingappschema               pcbuildingapp_user    false    222            Z           0    0    pcpart_partID_seq    SEQUENCE SET     O   SELECT pg_catalog.setval('pcbuildingappschema."pcpart_partID_seq"', 36, true);
          pcbuildingappschema               postgres    false    219            �           2606    16451 *   pcbuildlist_pcpart pcbuildlist_pcpart_pkey 
   CONSTRAINT     }   ALTER TABLE ONLY pcbuildingappschema.pcbuildlist_pcpart
    ADD CONSTRAINT pcbuildlist_pcpart_pkey PRIMARY KEY (listpartid);
 a   ALTER TABLE ONLY pcbuildingappschema.pcbuildlist_pcpart DROP CONSTRAINT pcbuildlist_pcpart_pkey;
       pcbuildingappschema                 pcbuildingapp_user    false    223            �           2606    16454 ,   pcbuildlist_pcpart pcbuildlist_pcpart_unique 
   CONSTRAINT     ~   ALTER TABLE ONLY pcbuildingappschema.pcbuildlist_pcpart
    ADD CONSTRAINT pcbuildlist_pcpart_unique UNIQUE (listid, partid);
 c   ALTER TABLE ONLY pcbuildingappschema.pcbuildlist_pcpart DROP CONSTRAINT pcbuildlist_pcpart_unique;
       pcbuildingappschema                 pcbuildingapp_user    false    223    223            �           2606    16396    useraccount unique_accountID 
   CONSTRAINT     p   ALTER TABLE ONLY pcbuildingappschema.useraccount
    ADD CONSTRAINT "unique_accountID" PRIMARY KEY (accountid);
 U   ALTER TABLE ONLY pcbuildingappschema.useraccount DROP CONSTRAINT "unique_accountID";
       pcbuildingappschema                 postgres    false    218            �           2606    16427    pcpart unique_partname 
   CONSTRAINT     b   ALTER TABLE ONLY pcbuildingappschema.pcpart
    ADD CONSTRAINT unique_partname UNIQUE (partname);
 M   ALTER TABLE ONLY pcbuildingappschema.pcpart DROP CONSTRAINT unique_partname;
       pcbuildingappschema                 postgres    false    220            �           2606    16432     pcbuildlist unique_pcbuildlistid 
   CONSTRAINT     v   ALTER TABLE ONLY pcbuildingappschema.pcbuildlist
    ADD CONSTRAINT unique_pcbuildlistid PRIMARY KEY (pcbuildlistid);
 W   ALTER TABLE ONLY pcbuildingappschema.pcbuildlist DROP CONSTRAINT unique_pcbuildlistid;
       pcbuildingappschema                 postgres    false    221            �           2606    16434 "   pcbuildlist unique_pcbuildlistname 
   CONSTRAINT     u   ALTER TABLE ONLY pcbuildingappschema.pcbuildlist
    ADD CONSTRAINT unique_pcbuildlistname UNIQUE (pcbuildlistname);
 Y   ALTER TABLE ONLY pcbuildingappschema.pcbuildlist DROP CONSTRAINT unique_pcbuildlistname;
       pcbuildingappschema                 postgres    false    221            �           2606    16407    pcpart unique_pcpart 
   CONSTRAINT     c   ALTER TABLE ONLY pcbuildingappschema.pcpart
    ADD CONSTRAINT unique_pcpart PRIMARY KEY (partid);
 K   ALTER TABLE ONLY pcbuildingappschema.pcpart DROP CONSTRAINT unique_pcpart;
       pcbuildingappschema                 postgres    false    220            �           2606    16398    useraccount unique_username 
   CONSTRAINT     g   ALTER TABLE ONLY pcbuildingappschema.useraccount
    ADD CONSTRAINT unique_username UNIQUE (username);
 R   ALTER TABLE ONLY pcbuildingappschema.useraccount DROP CONSTRAINT unique_username;
       pcbuildingappschema                 postgres    false    218            �           2606    16435    pcbuildlist accountid    FK CONSTRAINT     �   ALTER TABLE ONLY pcbuildingappschema.pcbuildlist
    ADD CONSTRAINT accountid FOREIGN KEY (accountid) REFERENCES pcbuildingappschema.useraccount(accountid);
 L   ALTER TABLE ONLY pcbuildingappschema.pcbuildlist DROP CONSTRAINT accountid;
       pcbuildingappschema               postgres    false    4765    221    218            �           2606    16460 /   pcbuildlist_pcpart pcbuildlist_pcpart_partid_fk    FK CONSTRAINT     �   ALTER TABLE ONLY pcbuildingappschema.pcbuildlist_pcpart
    ADD CONSTRAINT pcbuildlist_pcpart_partid_fk FOREIGN KEY (partid) REFERENCES pcbuildingappschema.pcpart(partid) NOT VALID;
 f   ALTER TABLE ONLY pcbuildingappschema.pcbuildlist_pcpart DROP CONSTRAINT pcbuildlist_pcpart_partid_fk;
       pcbuildingappschema               pcbuildingapp_user    false    220    223    4771            �           2606    16455 *   pcbuildlist_pcpart pcbuildlist_pcpartid_fk    FK CONSTRAINT     �   ALTER TABLE ONLY pcbuildingappschema.pcbuildlist_pcpart
    ADD CONSTRAINT pcbuildlist_pcpartid_fk FOREIGN KEY (listid) REFERENCES pcbuildingappschema.pcbuildlist(pcbuildlistid) NOT VALID;
 a   ALTER TABLE ONLY pcbuildingappschema.pcbuildlist_pcpart DROP CONSTRAINT pcbuildlist_pcpartid_fk;
       pcbuildingappschema               pcbuildingapp_user    false    223    221    4773                       826    16443     DEFAULT PRIVILEGES FOR SEQUENCES    DEFAULT ACL     Y   ALTER DEFAULT PRIVILEGES FOR ROLE postgres GRANT ALL ON SEQUENCES TO pcbuildingapp_user;
                        postgres    false            
           826    16442    DEFAULT PRIVILEGES FOR TABLES    DEFAULT ACL     V   ALTER DEFAULT PRIVILEGES FOR ROLE postgres GRANT ALL ON TABLES TO pcbuildingapp_user;
                        postgres    false            D   �   x�m�1�0���8''ZZ��1])�I�迗JL�\./o��=�@�7�W�7M��`{���8��k��譀�\F��8_J�f	�v�-���@����֙_��d	1[J&TN%�c���ZID:Ӧ's���8�������?6��p��A�O~(K�C�%�� fH�      F   �   x�%��!Dѳ�5 ����ҟ۫����c��W4�׃ɗ�]��C��|Ȼ_+��H)q���d�T�����_�����v_ f��A;M4阗rɽ='޸gS��=|��h���9��n.�      C   �  x��ZYs�~v~�*O�
0�,o�ƒ�TC��PSSE9F!�6c�,=����ހ���tݗKGۧ�,ґ)]yv�����z�R{x�ڞ�d~\!�X6�)�}����������i�>X�,p���^��j�+��z�[��.D���y��9�-�[F��[Mt6�^>q[t={d��1�"XS��Pk��Я�#*�\Ǫ,��p�i<wV��D��'�a�WEQo�ge���Vk�彳t�W.��6�e��������`�����'X�ڞX �Ӿ�@�a]�{��n/���4j,,�VL�!� ��������qc���_g��Κ��OE��>ÿެ�Zfc�Q~c �ʍQ�����ܱB�ߎ��;��¶z`[S�&����F� �~t�j(����?�[X�<2e-}N+4d�_� @� ���q�[���ʒ��KH�>h�]�:��:���A�P��y)⯪�$u Rs4�Q��K��o@�^����9j��T!�����p¿�ф#�d��2Z1~.�9�Rs�A��o�E:2a�S�����R�M���:�nY0Ͷ�G�V���y���� ��g֜Wc>�-���)��u?�r�����y\��0;tfA�����5�+W��l�V����35�6���G���x���~B�T/�>C�Y�Ԕ�/Q!����5MaP�0(ZBs0${��B!%,�ف�֐��Bۇ��O���oJ���~�� P��Ё��ױ��(�q�r��Ct�)ҁ�Ă~(T��,��א�?tSF����G�h*��ڽg��TJ*I��*�΃Ç�s��OwBa?:�x�7^��HnTFj��B,�̂��٪�5��0IW����ʝ�xm��1�Y��7�&%4*Ag/��9������ټ��Yː}E�~�ʆ�ۃ�f�Yp{2s��NO�3�ٿ6Vb��|��	>�v�f�ti2�'�s���$����~���pf���&���0!��䅠	̢ޮ!,+��f��!QǁZ�����6k.�� ����(�1���+���W�y�l4��D��w�E-M�����8G-.%UJa��Z��i���ЊwQ�.���IE����	��#Ie���r
�sr���=�S�P�F�O)�����n�������t݆�]z���h2ET�d��d������VO����<C�bwI����ti��x��̞�y��[[��eqG/F"���S�%s�=6M>7�[3���;��Wp�$ђ�!�i��%�qľ��!�=�e�|)����q9�$���u�'��4���f����
ip�ffB�lq���zɪ�����Zj����@=��z#��~k��`c][�`�.�	^u�{�LZ���K�l]j�Y��tvu;��m�-;�=���n�D��C���u
��~F%d���-���Pa�R'$r�Ͼ�mg�ig��*g�xw�l �Ԇ���*h������l��f�{!Pe��A G���8��ҘY�P��}���-�MW���II�R�`��\�tqL1��鼅���O�edޮ� ��P������Y�c�䢎�pB��ά���h|e��4�҂�T%��G��
�O�v��*e����:�ף��%�k�h|3�	����.�^���I�o����,���,�"�-�8��9�ﵿ���Ux���N��DM�cG�CgD&�'��!o���,_ӛ؜���H��
C+���mE����@�گQ }���2Z�7�,-?���f�|E��L��K� B��$�J�_�!C�n�4xH����9h$\^��(i�0�&����$'d	(���)]�o�_���Cܬ�c����T%���i;�K:�g�ӷ�{����	��L�g���������6�tRf����M8�b����`��{ ���0���	[���樻�a����f�֫����\\��$uw�8�7A8{p���m�k���ܰ�Z^z������DV&�\K�Ci�Vn��y߉mIZ��N)�CT��%�:RM��(��3q]�=�%���������~���4�C�eo·��U�e�`�5�����������S� xGE�z��*'=�D"s�,�Y����Oש�`#��(J>�3��ۘ��c4U%�W��}r"Héi?��(��uԾ��([�@�����i�#�U�]H�El+W%4�${>:�W��2��bG�fϒ�fE�"I�%��J}�rQ�A7�����z�} ���-��T%dĆ,�7�<q��C��j>���]}A���F�������n�ɥr�4�!��j���90�#L� 1�m͠�EMM��}���*~E�����_ ��o�s�MQ�"KWL'賉.S��K�����豞,ra54v�ґɂ,���sޙQE����gf��31�33���,�(�㝙p_�+�M?"��w�6��L(�^�:�1+{/�Kx��L���2�t� ��;,p.���Ѵ�<_)���� !<W��2�����Ł\F��J�;�� o�^`e�^��Bo3�0R*|oыA^�c��x�lc��+�{)����a�^D<��["5�}���r�p����K��D�����z�܉_��A��{��F<\�E6DP���������1�=���Z�b����Tl�}�l��K�F�l���b��P��E�!��?_�#���\'r�0����ƉSC���z��ܼ���'�b^��oy|PNO5]���h���_|x��s�><0~�wz1���5�ތS���q5���v�l�}��`J�`!���#r��z�p�>��N��/����[�"&��
�i��g�r5:�{5�m}J/D*�n'�o�H�*!����ʓ�L/��z��m�K�M���"�����+"E�[��/|�k%}����Ҫ<�@�
 ��)���J��jL�$�$���`Bs�(�L�TX|� �a1"TT��u�Ƈ��?TA �&�UH�K,+�%���B!C�2����!����B�AD GK�9��Z��-+F�=bT�x��T�7�E9�@����]��Ț3�E�)����d��&'(��ȥ$o�3Q��]�V����(��j�/�<f��qrr�?H~\)      A   �   x�%�KJE1 ���b�i>Mv �'I���'8p���G���ϯg�%��N�C��Z��i7n+��U�&�<�Ӎ2��c��V��Xxa�66��(�+D�`vEm]7���J�@8g�ȬA����i�H���B,�3|���ًw魅�J��`G��Ŷi���1~G�?�     