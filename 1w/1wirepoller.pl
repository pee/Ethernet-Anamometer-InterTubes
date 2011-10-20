#!/usr/bin/perl -w
#
#
use strict;
use warnings;

use OWNet;

use CGI;
use CGI::Carp;
use Data::Dumper;
use JSON;
use LWP::UserAgent;


my $HUMIDITY 	= "/uncached/26.0148E7000000/humidity";
my $LCOUNTER 	= "/uncached/1D.B28A0C000000/counters.B";
my $TEMP0 	= "/uncached/10.F73ECB010800/temperature";
my $TEMP1 	= "/uncached/26.0148E7000000/temperature";

my $ows = OWNet->new("127.0.0.1:4304 -v -F");

my $json = JSON->new->allow_nonref;

my %reportData = ();
my $entry = undef;

$reportData{'method'} = 'Report';
$reportData{'sensorID'} = '26_0148E7000000';
$reportData{'readings'} = [];

my $hv = $ows->read($HUMIDITY);
if ( ! defined($hv) ) {
	warn "Unable to read humidity sensor $HUMIDITY\n";
} else {
#	print("Humidity: $hv\n");

	$entry = {};
	$entry->{'type'} = 'humidity';
	$entry->{'units'} = '%RH';
	$hv = $hv + 0;
	$entry->{'value'} = $hv;

	push ( @{ $reportData{'readings'}}, $entry);
}

my $t1 = $ows->read($TEMP1);
if ( ! defined($t1) ) {
	warn "Unable to read temperature sensor $TEMP1\n";
} else {
#	print("Temp1   : $t1\n");
	$entry = {};
	$entry->{'type'} = 'temperature';
	$entry->{'units'} = 'degrees celcius';
	$t1 = $t1 + 0;
	$entry->{'value'} = $t1;

	push ( @{ $reportData{'readings'}}, $entry);
}

my $jData = $json->encode(\%reportData);

#print "jData:$jData\n";

sendData($jData);


%reportData = ();
$entry = undef;

$reportData{'method'} = 'Report';
$reportData{'sensorID'} = '10_F73ECB010800';
$reportData{'readings'} = [];

my $t0 = $ows->read($TEMP0);
if ( ! defined($t0) ) {
	warn "Unable to read temperature sensor $TEMP0\n";
} else {
#	print("Temp0   : $t0\n");
	$entry = {};
	$entry->{'type'} = 'temperature';
	$entry->{'units'} = 'degrees celcius';
	$t0 = $t0 + 0;
	$entry->{'value'} = $t0;

	push ( @{ $reportData{'readings'}}, $entry);
}

$jData = $json->encode(\%reportData);

#print "jData:$jData\n";

sendData($jData);


%reportData = ();
$entry = undef;

$reportData{'method'} = 'Report';
$reportData{'sensorID'} = '1D_B28A0C000000';
$reportData{'readings'} = [];
my $lc = $ows->read($LCOUNTER);
if ( ! defined($lc)) {
} else {
#	print("Strikes : $lc\n");
	$entry = {};
	$entry->{'type'} = 'strikes';
	$entry->{'units'} = 'radio bursts';
	$lc = $lc + 0;
	$entry->{'value'} = $lc;

	push ( @{ $reportData{'readings'}}, $entry);
}


$jData = $json->encode(\%reportData);

#print "jData:$jData\n";

sendData($jData);

exit;

##########################################################################################
sub sendData {

	my $jData = shift;

	my $ua = LWP::UserAgent->new;

	my $req = HTTP::Request->new(PUT => 'http://10.1.1.18:8080/sc4/report');
	$req->content_type('application/json');
	$req->content($jData);

	my $res = $ua->request($req);

	if ( ! $res->is_success) {
		warn "Mooby Failed:", $res->status_line, "\n";
	}

#	$req = HTTP::Request->new(PUT => 'http://10.1.1.7:8080/sc4/report');
#	$req->content_type('application/json');
#	$req->content($jData);

#	$res = $ua->request($req);

#	if ( ! $res->is_success) {
#		warn "Dev Failed:", $res->status_line, "\n";
#	}

}







